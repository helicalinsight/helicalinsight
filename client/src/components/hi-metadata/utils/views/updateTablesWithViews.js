import { cloneDeep } from 'lodash-es';

export const updateTablesWithViews = ({ tables = false, views = false, activeView = false }) => {
    if (!tables || !views || !activeView) return
    tables = cloneDeep(tables)
    let currentView = views.filter(eachView => eachView.uuid === activeView)
    if (currentView?.length !== 1) return
    currentView = currentView[0]
    let currentTable = Object.values(tables).filter(eachTable => {
        return eachTable.id === currentView.id
    })
    if (currentTable.length !== 1) {
        console.log('table is not already available -- so adding new view as table')
        tables[currentView.name] = {
            ...currentView,
            columnsFetched: true,
            keyName: currentView.name,
            alias: currentView.alias || currentView.name,
            category: 'view',
        }
        return tables
    }
    currentTable = currentTable[0]
    tables[currentTable.keyName].columns = currentView.columns
    tables[currentTable.keyName].columnsFetched = true
    tables[currentTable.keyName].alias = currentView.alias
    if(currentView.name !== currentTable.keyName) {
        tables[currentTable.keyName].name = currentView.name
        tables[currentTable.keyName].nameWithConnId = `${currentView.name}_${tables[currentTable.keyName].connId}`
        delete Object.assign(tables, {[currentView.name]: tables[currentTable.keyName] })[currentTable.keyName];
        tables[currentView.name].keyName = currentView.name;
    }

    return tables
}