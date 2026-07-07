export const getCrossTabBooleanConfig=({crosstab})=>{
    let grandTotals={
            showGrandTotals:false,
            showRowGrandTotals: false,
            showColumnGrandTotals: false,
    }
    let subTotals={
        showSubTotals:false,
        showRowSubTotals:false,
        showColumnSubTotals: false,
    }
    let totalsPositions={
    } 


    if(crosstab?.showGrandTotals && crosstab?.showSubTotals){
        grandTotals.showGrandTotals=true
        grandTotals.showRowGrandTotals=true
        grandTotals.showColumnGrandTotals=true
        subTotals.showSubTotals=true
        subTotals.showRowSubTotals=true
        subTotals.showColumnSubTotals=true
    }
    if(crosstab?.showGrandTotals){
        grandTotals.showGrandTotals=true
        grandTotals.showRowGrandTotals=true
        grandTotals.showColumnGrandTotals=true
    }
    if(crosstab?.showSubTotals){
        subTotals.showSubTotals=true
        subTotals.showRowSubTotals=true
        subTotals.showColumnSubTotals=true
    }
    if(crosstab?.showRowGrandTotals){
        grandTotals.showGrandTotals=true
        grandTotals.showRowGrandTotals=true
        grandTotals.showColumnGrandTotals=false
    }
    if(crosstab?.showColumnGrandTotals){
        grandTotals.showGrandTotals=true
        grandTotals.showRowGrandTotals=false
        grandTotals.showColumnGrandTotals=true
    }
    if(crosstab?.showRowSubTotals){
        subTotals.showSubTotals=true
        subTotals.showRowSubTotals=true
        subTotals.showColumnSubTotals=false
    }
    if(crosstab?.showColumnSubTotals){
        subTotals.showSubTotals=true
        subTotals.showRowSubTotals=false
        subTotals.showColumnSubTotals=true
    }
    // if(crosstab?.subTotalsPosition!=="Auto"){
    //     totalsPositions.subTotalsPosition=crosstab?.subTotalsPosition
    // }
    if(crosstab?.grandTotalsPosition){
    totalsPositions.grandTotalsPosition=crosstab?.grandTotalsPosition
    }

return {
    ...grandTotals,  ...subTotals,...totalsPositions
} 
}