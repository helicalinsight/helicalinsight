// import { fromJS } from 'immutable';
import jsonata from "jsonata";
import lodash from "lodash-es";
import deepdash from "deepdash-es";
const _ = deepdash(lodash);

// const data = [
//   {
//     name: "Naresh",
//     type: "folder",
//     children: [
//       {
//         path: "1633601282357/abc",
//         name: "abc",
//         type: "folder",
//         children: [
//           {
//             extension: "hr",
//             name: "2a9f1701-4a30-4980-bd22-bd552663b8ba.report",
//             type: "file",
//             title: "3773 date asc",
//           },
//           {
//             extension: "efw",
//             name: "9439ab9a-bf7e-4907-9a91-765530076251.efw",
//             type: "file",
//             title: "hiselect 2 hidataman",
//           },
//         ],
//       },
//       {
//         extension: "efwdd",
//         name: "ec8c38f3-1049-433a-b7eb-d6d94299925b.efwdd",
//         type: "file",
//         title: "efw",
//       },
//       {
//         extension: "hr",
//         name: "1beb3cc4-78b1-4815-bd3f-a4b768b9a795.hr",
//         type: "file",
//         title: "all",
//       },
//     ],
//   },
// ];
const conditionConfig = {
  equals: "=",
  "not equals": "!=",
  regex: "~>",
};
class Query {
  data = [];
  result = null;
  selectColumns = [];
  sortColumns = [];
  predicate = {};
  orPredicates = [];
  andPredicate = {};
  arrayFuntions = {
    // structure is fixed
    distinct: false,
    count: false,
  };
  expressionStr = "";
  preserveStructureKey = "children";
  version = () => {
    return "JSONata 1.8.5";
  };
  select = (...columns) => {
    this.selectColumns = columns;
    return this;
  };
  from = (data) => {
    this.data = data;
    return this;
  };
  where = (column, condition, value) => {
    this.predicate = { column, condition, value };
    return this;
  };
  and = (column, condition, value) => {
    this.andPredicate = {
      andColumn: column,
      andCondition: condition,
      andValue: value,
    };
    return this;
  };
  or = (column, condition, value) => {
    const orConditions = this.orPredicates;
    orConditions.push({ column, condition, value });
    return this;
  };
  distinct = () => {
    this.arrayFuntions = { ...this.arrayFuntions, distinct: true };
    return this;
  };
  count = () => {
    this.arrayFuntions = { ...this.arrayFuntions, count: true };
    return this;
  };
  sort = (sortKey, sortOrder) => {
    switch (sortOrder) {
      case "ascending":
        this.result = this.data.sort((a, b) =>
          a[sortKey].localeCompare(b[sortKey])
        );
        return this;
      case "descending":
        this.result = [...this.data]
          .sort((a, b) => a[sortKey].localeCompare(b[sortKey]))
          .reverse();
        return this;
      default:
        this.result = this.data;
        return this;
    }
  };
  limit = () => {
    // to do
  };
  // doSort = () => {
  //   this.result = _.orderBy(
  //     this.result,
  //     _.map(this.sortColumns, "column"),
  //     _.map(this.sortColumns, "order")
  //   );
  // };
  expression = (expression) => {
    this.expressionStr = expression;
    return this;
  };
  executeJsonata = () => {
    const result = jsonata(this.expressionStr).evaluate(this.data);
    this.result = result ? JSON.parse(JSON.stringify(result)) : [];
  };
  applyWhere = (column, condition, value) => {
    let whereExpression = null;
    const conditionSymbol = conditionConfig[condition];
    if (column && condition && value) {
      whereExpression = `${column} ${conditionSymbol} ${
        condition === "regex" ? `/${value}/i` : JSON.stringify(value)
      }`;
    }
    return whereExpression;
  };
  filterSelect = () => {
    let filterObjString = "{";
    const columns = this.selectColumns;
    if (columns.length === 0) return null;
    if (columns.length === 1) return `${columns[0]}`;
    if (columns.length > 0) {
      columns.forEach((column, idx) => {
        filterObjString +=
          idx === columns.length - 1
            ? `"${column}": ${column}}`
            : `"${column}": ${column},`;
      });
    }
    return filterObjString;
  };
  generateJsonataExpression = () => {
    let expression = "**"; // using ** for any nested depth
    let { column, condition, value } = this.predicate;
    const whereExpression = this.applyWhere(column, condition, value); // generating where expression for ".where"
    const selectExpression = this.filterSelect(); // generating expression for ".select"
    if (
      Object.keys(this.predicate).length > 0 ||
      this.selectColumns.length === 1
    ) {
      if (whereExpression) {
        expression += "[" + whereExpression;
        // looping ".or" predicates and appending to available where expression
        if (this.orPredicates.length > 0) {
          for (let i = 0; i < this.orPredicates.length; i++) {
            const orPred = this.orPredicates[i];
            expression +=
              " or " +
              this.applyWhere(orPred.column, orPred.condition, orPred.value);
          }
        }
        expression += "]";
      }
      if (selectExpression) expression += `.${selectExpression}`;
    } else {
      if (selectExpression) expression += `${selectExpression}`;
    }
    this.expressionStr = this.applyArrayFunctions(expression);
  };
  applyArrayFunctions = (expression) => {
    const appliedArrayFunctions = _.pickBy(
      this.arrayFuntions,
      function (value) {
        return value;
      }
    );
    if (Object.keys(appliedArrayFunctions).length > 0) {
      const arrayFuncs = appliedArrayFunctions;
      for (let func in arrayFuncs) {
        expression = `$${func}(${expression})`;
      }
    }
    return expression;
  };
  execute = () => {
    if (this.selectColumns.length === 1 && this.selectColumns[0] === "*") {
      this.preserveStructureFilter();
    } else {
      this.generateJsonataExpression();
      this.executeJsonata();
    }
    return this.result;
  };
  getQueryString = () => {
    this.generateJsonataExpression();
    return this.expressionStr;
  };
  prepareResult = (callback) => {
    this.execute();
    this.result =
      typeof callback === "function" ? callback(this.result) : this.result;
    return this;
  };
  whereClause(col, cond, val) {
    return (ele) => {
      if (cond === "=") return ele[col] === val;
      if (cond === "!=") return ele[col] !== val;
    };
  }
  preserveKey(key) {
    this.preserveStructureKey = key;
    return this;
  }
  preserveStructureFilter() {
    let { column, condition, value } = this.predicate;
    condition = conditionConfig[condition];
    const d = this.data;
    const where = this.whereClause;
    this.result = _.filterDeep(d, where(column, condition, value), {
      childrenPath: this.preserveStructureKey,
    });
  }
}

// need outcome for jsonata

//TODO: add preseverConfig, add regex, make "where" clause implementation common for both jsonata and deepdash.
// // Get preserverd structure where extension equals hr
// // deepdash, using "*" for select
//console.log('res1', new Query().from(data).select("*").where("extension", "equals", "hr").execute());
////console.log('res1', new Query().from(data).select("*").preserveKey("customKey").where("extension", "equals", "hr").execute());

// //console.log(new Query.from(dataSource).where("extension", "equals", "123")).execute(); uhsd  sdisiiii consile.log()hush  res2 new Quetruwil.com hshohuuh nxijshbdksf

// // Get all results where extension equals hr, structure doenst matter
// // **[extension="hr"]
//console.log('res1', new Query().from(data).where("extension", "equals", "hr").execute())

// // Get only name and type from results where extension equals hr
// // **[extension="hr"].{"name": name, "type": type}
//console.log('res1', new Query().from(data).select("name","type").where("extension", "equals", "hr").execute())

// TODO: add distinct(), totalCount(), sort()
// // Get all names from data
// // **.name
//console.log('res1', new Query().from(data).select("name").execute())

// // Get all names and types from data
// // **{"name": name, "type": type} // here name and type are arrays which contain accumulated values
//console.log('res1', new Query().from(data).select("name","type").execute())

// // Get all results where extension equals hr or efw
// // **[extension="hr" or extension="efw"]
//console.log('res1', new Query().from(data).where("extension", "equals", "hr").or("extension", "equals", "efw").execute())

// regex match
//console.log('res1', new Query().from(data).where("name", "regex", "2a9").execute())

// get distinct results
//console.log('res1', new Query().from(data).where("name", "regex", "2a9").count().distinct().execute())
// window.Query = new Query()

export default Query;
