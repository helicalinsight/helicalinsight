export function checkInvalidJoins(joins, tables) {

    let invalidJoins = [];
    invalidJoins = joins.filter(join => {
        let joinLeftColumn = true;
        let joinRightColumn = true;
        if(join.action === 'delete') return false;
        if(!join.left?.tableId || !join.right?.tableId || !join.left?.column || !join.right?.column){
            return true;
        }
        const joinLeftTable = Object.values(tables).find(table => (table.id === join.left?.tableId || table.cacheId === join.left?.tableId) && table.connId === join.left?.dbId);
        const joinRightTable = Object.values(tables).find(table => (table.id === join.right?.tableId || table.cacheId === join.right?.tableId) && table.connId === join.right?.dbId);
        if(!joinLeftTable || !joinRightTable) return true;
        if(joinLeftTable.columns) {
            joinLeftColumn = Object.values(joinLeftTable.columns).find(col => col.name === join.left.column);
        }
        if(joinRightTable.columns) {
            joinRightColumn = Object.values(joinRightTable.columns).find(col => col.name === join.right.column);
        }
        if(!joinLeftColumn || !joinRightColumn) return true;
        return false;
    });
    return invalidJoins;
}