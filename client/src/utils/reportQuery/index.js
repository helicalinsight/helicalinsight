// import prepareFilterFormData from './filterFormData'
import util from './utils';
import generateReportFormData from './reportFormData'
import constants from './constants';
import requests from '../../base/requests'
import { uuid } from '../uuid'

const getInitialData = (self) => {
    self.formData = null;
    self.formDataFor = ''
    // self.dataSource = util.validateDataSource(dataSource) ? dataSource : null
    self.column = null
    // self.columns = null // this is for preparing report/query formdata - will combile this.column and this.columns later
    self.columns = [] // this is for preparing report/query formdata - will combile this.column and this.columns later
    self.colAlias = null
    self.limitBy = 50
    self.offsetVal = null
    self.database = null
    self.prependTableNameToAlias = false
    self.sample = 'sample'
    self.mode = 'create-quick'
    self.groupByInfo = []
    self.floatingType = 'discrete'
    // self.hiddenIncludeInResultSet = false
    // self.applyBeforeAggregate = false
    self.isNormalTable = true
    self.orderByColumn = false
    self.aggregateInfo = {}
    self.orignalAggregateInfoOrder = {}
    self.orderByInfo = {}
    self.filtersInfo = {
        filters: [],
        customFilterExpression: '',
        customHavingExpression: '',
        customFilterExpressionObj: {},
        customHavingExpressionObj: {},
        filterOperators: {},
        bracketOpen: false,
        bracketClose: false,
    }
    self.analyticsInfo = null
    self.isRefreshSet = false
    self.selectBuilder = {}
    self.hiddenColumns = []
    self.hideAndIncludeInResultSetColumns = []
    self.misc = {
        aliasForRaw: ''
    }
    self._requestId = uuid('short')
    self.expression = {
        filter : false,
        having : false
    }
    return self
}
const addExternalFns = (self) => {
    if (constants?.functions?.aggregate) { // adding aggregate functions
        let aggregate = constants.functions.aggregate
        Object.keys(aggregate).map((aggFn => {
            self[aggFn] = (col) => {
                /**
                 * check if column is available -- start
                 * if not create a new column using select
                 */
                let actualArg = col
                if (self.selectBuilder?.inSelectBuilder && !self.selectBuilder?.superColumn && !self.selectBuilder?.fromFnBuilder) {
                    if (typeof col == 'string') {
                        if (!self.selectBuilder.isColumnAdded) {
                            let alias = `${new Date().getTime()}`
                            self.select(col, self.selectBuilder?.fromFnBuilderAlias || alias)
                            self.selectBuilder.isColumnAdded = true
                            self.selectBuilder.superColumn = col
                            self.selectBuilder.superAlias = alias
                        }
                    }
                } // creating column if not available - end
                if (self.selectBuilder?.paramsMapping && util.checkIfObject(self.selectBuilder?.paramsMapping) && self.selectBuilder?.paramsMapping[col]) {
                    col = self.selectBuilder?.paramsMapping[col][0]
                }
                col = self.selectBuilder?.superColumn ? self.selectBuilder.superColumn : col // updating col with actual column incase of select builder
                if (!(col in self.aggregateInfo)) {
                    self.aggregateInfo[col] = {}
                    self.orignalAggregateInfoOrder[col] = {}
                }
                if (!(Object.values(self.aggregateInfo[col]).includes(aggregate[aggFn]))) {
                    // if (!(Object.keys(self.aggregateInfo[col]).includes(aggregate[aggFn]))) {
                    self.aggregateInfo[col][Object.keys(self.aggregateInfo[col]).length + 1] = aggregate[aggFn]
                    self.orignalAggregateInfoOrder[col][Object.keys(self.orignalAggregateInfoOrder[col]).length + 1] = aggregate[aggFn]
                }
                if (self.aggBuilder?.inBuilder || self.selectBuilder.inSelectBuilder) {
                    // in self builder reverse the order of functions
                    if (self?.aggBuilder && util.checkIfObject(self?.aggBuilder)) { self.aggBuilder.column = col }
                    let aggList = Object.values(self.orignalAggregateInfoOrder[col])
                    aggList.reverse()
                    let result = {}
                    aggList.map((each, index) => {
                        result[index + 1] = each
                    })
                    self.aggregateInfo[col] = result
                    return actualArg
                }
                return self

            }
            return null
        }))
    }
    if (constants?.functions?.db) {
        let dbf = constants.functions.db
        Object.keys(dbf).map((dbFn) => {
            self[dbFn.toLowerCase()] = (...args) => {
                return util.handleApplyDBFunction(args, self, dbf[dbFn])
                // return self
            }
            return null
        })
    }
}
class ReportQuery {
    constructor(dataSource = null, dispatch) { //pass metadata here
        this.dataSource = util.validateDataSource(dataSource) ? dataSource : null
        this.dispatch = dispatch
        getInitialData(this)
        addExternalFns(this)
        if (!Array.isArray(dataSource.useDBFuntion) && typeof dataSource.useDBFuntion === 'object') {
            util.updateDatabaseFunctions(dataSource.useDBFuntion, this)
            delete this.dataSource.useDBFuntion
        }
    }
    generateFilterFormData(payload = {}) {
        this.formDataFor = 'filters'
        if (this.dataSource) {
            payload.dataSource = this.dataSource
        }
        if (this.column) {
            payload.column = this.column
        }
        if (this.colAlias) {
            payload.alias = this.colAlias
        }
        if (this.limitBy) {
            payload.limitBy = this.limitBy
        }
        if (this.offsetVal) {
            payload.offset = this.offsetVal
        }
        if (process.env.JEST_WORKER_ID !== undefined) {
            // return prepareFilterFormData(payload, this)
        }
        return this
    }
    resetData = () => {
        return getInitialData(this)
    }
    getFD() {
        if (process.env.JEST_WORKER_ID !== undefined) {
            // return prepareFilterFormData(payload, this)
        }
        return this
    }
    debug() {
        return this.formData
    }
    show() {
        return this.formData
    }

    /**
     * .select('HIUSER.travel_details.destination', 'destination')
     * .select((builder)=>{
            builder.concat(builder.lower('travel_details.destination'), builder.upper('travel_details.source'))
        }
        , 'aliasName2')
     */
    select() { // this takes columns and add them to this

        //check if the select is called as .select('col', 'alias')
        let payload = []
        if (arguments.length == 2 && (typeof arguments[0] == 'string' || typeof arguments[0] == 'object') && typeof arguments[1] == 'string' && arguments[1].length) {
            //checking if alias is already used
            let isAliasAlreadyUsed = false
            this.columns.map(eachCol => {
                if (!isAliasAlreadyUsed && eachCol.alias == arguments[1]) {
                    isAliasAlreadyUsed = true
                }
                return null
            })
            if (isAliasAlreadyUsed) {
                console.warn(`Alias name "${arguments[1]}" already used`)
            }
            if(typeof arguments[0] == 'object'){ //sending column ID along with column name
                !isAliasAlreadyUsed && payload.push({ column: arguments[0].name, alias: arguments[1], id: arguments[0].id });
            }else{
                !isAliasAlreadyUsed && arguments[0].length && payload.push({ column: arguments[0], alias: arguments[1] })
            }
        }
        //checking if select is called as .select({'travel_details.booking_platform':'bf', destination: 'dest'})
        else if (arguments.length == 1 && util.checkIfObject(arguments[0])) {
            let data = arguments[0]
            Object.keys(data).map((col) => {
                if (typeof col == 'string' && col.length && typeof data[col] == 'string' && data[col].length) {
                    payload.push({ alias: col, column: data[col] })
                }
                return null
            })
        }
        else if (arguments.length == 1 && Array.isArray(arguments[0])) {
            payload = arguments[0]
        } else if (arguments.length <= 2 && typeof arguments[0] == 'function' && typeof arguments[1] == 'string') {
            util.handleSelectBuilder(arguments, this, false, arguments[1])
            return this
        }
        else {
            console.error('Invalid arguments for select')
            return this
        }
        return util.handleSelectPayload(payload, this)
    }

    /**
     * .selectRaw('abs(sum("travel_details"."destination_id"))', 'alias')
     */
    selectRaw() { // this is for custom column
        util.handleAddRawSelect([...arguments], this)
        return this
    }

    from = (payload) => {
        util.handleSetDataBase(payload, this)
        return this
    }
    groupBy() {
        // util.handleGroupBy(payload, this)
        let allAliases = this.columns.map(eachCol => (eachCol.alias))
        let args = [...arguments]
        let groupBySet = new Set(this.groupByInfo || [])
        if (args.length) {
            args.map((arg) => {
                if (typeof arg == 'string' && allAliases.indexOf(arg) != -1) {
                    if (groupBySet.has(arg)) {
                        console.warn(`Already grouped on ${arg}`)
                    }
                    else {
                        groupBySet.add(arg)
                    }
                } else {
                    console.warn(`Can't groupby on ${arg}`)
                }
                return null
            })
        }
        this.groupByInfo = [...groupBySet]
        // if (typeof payload == 'string') {
        //     this.groupByInfo.push(payload)
        // }
        return this
    }
    orderBy() {
        /**
         * order by can be called in two wayd
         * oderbyBy(alias1, alias2, alias3...) - for all these columns asc is applied by default
         * or
         * orderBy({alias1 : 'asc/desc', alias2 : 'asc/desc'})
         */
        let args = [...arguments], manipulatedOrderBy = {}
        let allAliases = this.columns.map(eachCol => (eachCol.alias))
        if (args.length == 0) {
            console.warn(`Invalid arguments for orderBy`)
        }
        else if (args.length && typeof args[0] == 'string') {
            /**
             * if the oderby is called as orderby(alias 1, alias 2, ...)
             * then converting the above to {alias : 'asc, alias2 : 'asc}
             */
            args.map((key) => {
                if (typeof key == 'string') {
                    manipulatedOrderBy[key] = 'asc'
                }
                return null
            })
        }
        let obj = Object.keys(manipulatedOrderBy).length ? manipulatedOrderBy : args[0]
        Object.keys(obj).map((key) => {
            if (allAliases.indexOf(key) != -1) {
                if (typeof obj[key] == 'string' && ['asc', 'desc'].indexOf(obj[key]?.toLowerCase()) != -1) {
                    this.orderByInfo[key] = obj[key].toLowerCase()
                }
                else {
                    console.warn(`Invalid orderby argument for ${key}. Expected 'asc' or 'desc' but recieved ${obj[key]}`)
                }
            }
            else {
                console.warn(`Provided ${key} is not an alias name`)
            }
        })
        return this
    }
    reportFormData(arg = {}) {
        util.handleSelectPayload(this.columns, this)
        let payload = util.preparePayloadForReport(this)
        arg.payload = payload
        if (!payload) {
            console.warn('Some of the arguments are missing')
            return this
        }
        let { groupByInfo, orderByInfo, _requestId } = this
        arg.payload.misc = {
            groupByInfo,
            orderByInfo
        }
        arg.payload._requestId = _requestId
        this.formData = generateReportFormData(arg, this)
        if (this.isRefreshSet) {
            this.formData.refresh = true
    }
        this.formData = util.updateHiddenColsInFormdata({ formData: this.formData, self: this })
        if (this.analyticsInfo) {
            this.formData.analytics = this.analyticsInfo
        }
        if (arg.returnData) return this.formData
        return this
    }
    rawReportFormData(arg = {}) {
        // util.handleSelectPayload(this.columns, this)
        // let payload = util.preparePayloadForReport(this)
        // arg.payload = payload
        // if (!payload) {
        //     console.warn('Some of the arguments are missing')
        //     return this
        // }
        if (!'flux' in arg) {
            console.warn('Argument flux required for rawReportFormData')
            return this
        }
        arg.raw = true
        arg.payload = { dataSource: this.dataSource, _requestId: this._requestId }
        this.formData = generateReportFormData(arg, this)
        return this
    }
    alias(payload) {
        if (typeof payload == 'string') {
            if (this.selectBuilder?.superAlias) {
                this.selectBuilder.isAliasSet = true
                this.columns = this.columns.map((eachCol) => {
                    if (eachCol.alias == this.selectBuilder.superAlias) {
                        eachCol.alias = payload
                    }
                    return eachCol
                })

            }
            this.colAlias = payload
        }
        else {
            // console && console.warn('Please provide proper inputs')
        }
        return this
    }
    limit() {
        //limit accepts two arguments limit, offset. offset is optional
        if (typeof arguments[0] != 'undefined' && (typeof arguments[0] == 'number' && arguments[0] >= -1)) {
            this.limitBy = arguments[0] == -1 ? 'full' : arguments[0]
        }
        else {
            this.limitBy = 50
        }
        if (typeof arguments[1] != 'undefined' && (typeof arguments[1] == 'number' && arguments[1] >= 0)) {
            this.offsetVal = arguments[1]
        }
        return this
    }
    offset(payload) {
        if ((typeof payload == 'number' && payload >= 0)) {
            this.offsetVal = payload
        }
        else {
            // console && console.warn('Please provide proper inputs')
        }
        return this
    }
    functionBuilder(cb, alias = false) { // -> earlier it is named as dbfun
        if (typeof cb !== 'function') {
            console.warn(`functionBuilder accpets function as first argument but recieved "${typeof cb}"`)
            return this
        }
        if (typeof cb == 'function' && cb.toString().includes('.raw(')) {
            if (!alias || typeof alias != 'string' || alias.length < 1 || this.columns.map(eachCol => (eachCol.alias)).indexOf(alias) == -1) {
                console.warn('Please provide valid alias for functionBuilder funciton')
                return this
            }
            this.misc.aliasForRaw = alias
            cb(this)
            this.misc.aliasForRaw = ''
            return this
        }
        util.handleSelectBuilder(arguments, this, true, alias)
        return this
    }
    raw(payload) {
        util.handleRawFnBuilder({ payload, self: this, alias: this.misc.aliasForRaw })
        return this
    }
    where() {
        let __args = [...arguments]
        let config = {}
        if (util.checkIfObject([...__args].pop())) {
            config = __args.pop()
        }
        let args = __args
        // if(arguments.length == 1 && typeof arguments[0] == 'function') return this
        return util.handleWhereCondition(args, this, 'AND', config)
    }
    orWhere() {
        let __args = [...arguments]
        let config = {}
        if (util.checkIfObject([...__args].pop())) {
            config = __args.pop()
        }
        let args = __args
        return util.handleWhereCondition(args, this, 'OR', config)
    }
    whereIn() {
        // let args = [...arguments]
        let __args = [...arguments]
        let config = {}
        if (util.checkIfObject([...__args].pop())) {
            config = __args.pop()
        }
        let args = __args
        if (args.length == 2) {
            args = [args[0], 'IN', args[1]]
        }
        return util.handleWhereCondition(args, this, 'AND', config)
    }
    orWhereIn() {
        // let args = [...arguments]
        let __args = [...arguments]
        let config = {}
        if (util.checkIfObject([...__args].pop())) {
            config = __args.pop()
        }
        let args = __args
        if (args.length == 2) {
            args = [args[0], 'IN', args[1]]
        }
        return util.handleWhereCondition(args, this, 'OR', config)
    }
    whereNotIn() {
        // let args = [...arguments]
        let __args = [...arguments]
        let config = {}
        if (util.checkIfObject([...__args].pop())) {
            config = __args.pop()
        }
        let args = __args
        if (args.length == 2) {
            args = [args[0], 'notin', args[1]]
        }
        return util.handleWhereCondition(args, this, 'AND', config)
    }
    orWhereNotIn() {
        // let args = [...arguments]
        let __args = [...arguments]
        let config = {}
        if (util.checkIfObject([...__args].pop())) {
            config = __args.pop()
        }
        let args = __args
        if (args.length == 2) {
            args = [args[0], 'notin', args[1]]
        }
        return util.handleWhereCondition(args, this, 'OR', config)
    }
    setCustomFE(arg){
        this.expression.filter = arg
        return this
    }
    execute(arg) {
        let { callBack, errorBack,getApi, query = false } = arg || {}
        if (this.formData) {
            // formDataFor == 'filters' && requests.adhoc.fetchData(this.formData, (res) => {
            // fetchQuery(formData, callback = () => { }, errback = () => { }) {
            //     return servicesRequest("adhoc/report/generateQuery", formData, callback, errback);
            // }
            // requests.adhoc
            // query && 
            let apiInstance
            if(query){
                apiInstance = requests.hreport(this.dispatch).generateQuery(this.formData, null, (res) => {
                    if (typeof callBack === 'function') {
                        callBack(res)
                    }
                }, (err) => {
                    if (typeof errorBack === 'function') {
                        errorBack(err)
                    }
                })
            }else{
                apiInstance =requests.hreport(this.dispatch).fetchData(this.formData, null, (res) => {
                    if (typeof callBack === 'function') {
                        callBack(res)
                    }
                }, (err) => {
                    if (typeof errorBack === 'function') {
                        errorBack(err)
                    }
                })
            }
            if(typeof getApi === "function"){
                getApi(apiInstance)
            }
        }
        return this
    }
    refresh(refresh = true) {
        if (typeof refresh == 'boolean') {
            this.isRefreshSet = refresh
        }
        return this
    }
    hide() {
        util.handleHideColumns({ hide: true, data: [...arguments], self: this })
        return this
    }
    hideAndIncludeInResultSet() {
        console.log('in hide function called')
        util.handleHideColumns({ hideAndIncludeInResultSet: true, data: [...arguments], self: this })
        return this
    }
    getData(arg) {
        return this[arg]
    }
    analytics(arg) {
        this.analyticsInfo = arg
        return this
    }
    manipulateFormData(cb) {
        let result = undefined
        if (typeof cb === 'function') {
            result = cb({ ...this.formData })
            if (util.checkIfObject(result)) {
                this.formData = result
            }
        }
        if(this.tempFilterExpressions){
            if(this.tempFilterExpressions.customFilterExpression){
                this.formData.customFilterExpression = this.tempFilterExpressions.customFilterExpression
            }
            if(this.tempFilterExpressions.customHavingExpression){
                this.formData.customHavingExpression = this.tempFilterExpressions.customHavingExpression
            }
        }
        return this
    }
    setFilterExpression(expressions) {
        this.tempFilterExpressions = expressions
        return this
    }
    applyBeforeAggregate() {
        util.handleApplyBeforeAggregate({ data: [...arguments], self: this })
        return this
    }
    requestId(id) {
        if (typeof id === 'string') {
            this._requestId = id
        }
        return this
    }
    togglePrependTable(bool) {
        this.prependTableNameToAlias = bool
        return this
    }
}
export default ReportQuery
// let ReportQuery2 = new ReportQuery()
// let window = window || {}
window.ReportQuery = ReportQuery


// function loaderScript(scriptUrl) {
//     return new Promise(function (res, rej) {
//         let script = document.createElement('script');
//         script.src = scriptUrl;
//         script.type = 'text/javascript';
//         script.onError = rej;
//         script.async = true;
//         script.onload = res;
//         script.addEventListener('error', rej);
//         script.addEventListener('load', res);
//         document.head.appendChild(script);
//     })

// }

// const updateReportQueryApi = (cb) => {
//     loaderScript("http://localhost:8888/js/reportQuery.js")
//         .then(() => { cb && cb() })
//         .catch((e) => { console.error("error caught", e); });
// }
// window.updateReportQueryApi = updateReportQueryApi

// window.bootstrapAPI = () => {
//     let query = new ReportQuery({
//         "location": "1463377807724/1463377836985",
//         "metadataFileName": "e9be6771-995b-40eb-a01c-304857a100a1.metadata",
//     })

//     return query
// }




