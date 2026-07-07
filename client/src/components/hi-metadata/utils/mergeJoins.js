export const mergeJoins = ({
  fetchedJoins = [],
  existingJoins = [],
  tables,
}) => {
  if (!existingJoins.length) return fetchedJoins;
  //add default join property to joins
  // add cache tableId to joins
  // let result = JSON.parse(JSON.stringify(existingJoins));
  let storeJoins = addCacheTableIdToJoins(existingJoins, tables);
  let newlyFetchedJoins = addCacheTableIdToJoins(fetchedJoins, tables);
  try {
    newlyFetchedJoins.forEach((eachJoin) => {
      //keeping this isDefault property. as we may require it from B.E
      if (eachJoin.isDefault || true) {
        if (!checkJoinAlreadyExists(eachJoin, storeJoins)) {
          storeJoins.push(eachJoin);
        }
      } else {
        storeJoins.push(eachJoin);
      }
    //   if (!existingJoins.some((join) => join.id === eachJoin.id)) {
    //     storeJoins.push(eachJoin);
    //   }
    });
  } catch (error) {
    console.log("Error in mergeJoins.js file", error);
  }

  return storeJoins;
};

function addCacheTableIdToJoins(joins, tables) {
  let updatedJoins = JSON.parse(JSON.stringify(joins));
  updatedJoins = updatedJoins.map((join) => {
    let leftTable = findJoinTable(tables, join.left.tableId, join.left.dbId);
    let rightTable = findJoinTable(tables, join.right.tableId, join.right.dbId);
    join.left = {
      ...join.left,
      cacheTableid: leftTable.cacheId ?? leftTable.id,
    };
    join.right = {
      ...join.right,
      cacheTableid: rightTable.cacheId ?? rightTable.id,
    };
    return join;
  });

  return updatedJoins;
}

function findJoinTable(tables, tableId, dbId) {
  let joinTable = Object.values(tables).find(
    (table) =>
      (table.cacheId === tableId || table.id === tableId) &&
      table.connId === dbId
  );
  if (!joinTable) throw new Error("Join table not found in all tables");
  return joinTable;
}

function checkJoinAlreadyExists(fetchedJoin, existingJoins) {
  let isJoinExisting = false;
  isJoinExisting = existingJoins.some(
    (join) =>
      join.left.cacheTableid === fetchedJoin.left.cacheTableid &&
      join.right.cacheTableid === fetchedJoin.right.cacheTableid &&
      join.left.dbId === fetchedJoin.left.dbId &&
      join.right.dbId === fetchedJoin.right.dbId && 
      join.right.column === fetchedJoin.right.column &&
      join.left.column === fetchedJoin.left.column
  );
  return isJoinExisting;
}
