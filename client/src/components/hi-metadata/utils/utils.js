// This function genrates the new name/alias for name/alias that is not already in use in current metadata.
// it is used for changeAlias and duplicate actions in metadata tables/columns
export function genrateUniqueName({ allItems, item, currentAlias = false }){
	const lastCharacter = item.charAt(item.length - 1);
	const secondLastCharacter = item.charAt(item.length - 2);
	let index = 1;
	let name = item + '_' + index;

	if(Number.isInteger(Number(lastCharacter)) && secondLastCharacter === "_") {
		index = Number(lastCharacter)
		item = item.substring(0, item.length-2);
		name = item + '_' + index;
	}
	
	while(index) {
		// This condition is used in update alias action
		if(currentAlias && name === currentAlias) {
			return currentAlias;
		}
		if(!allItems.includes(name)) {
			return item + '_' + index;
		}
		index++;
		name = item + '_' + index;
	}
}

export function areJoinsFetched(joins, currentDbId) {
    if(joins.length) {
        let i = 0;
        for(i; i < joins.length; i++) {
            if(joins[i].left.dbId === currentDbId || joins[i].right.dbId === currentDbId) return true;
        }    
    }
    return false;
}