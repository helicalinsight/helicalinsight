// export const validateViewName = ({ name, views, activeView }) => {
//     if (name.startsWith(' ') || name.endsWith(' ')) {
//         return { isValid: false, msg: 'View name cannot start or end with space' };
//     }
//     if (!Array.isArray(views) || !views.length) {
//         return { isValid: false, msg: 'No views present' }
//     }
//     if (!activeView) {
//         return { isValid: false, msg: 'No view is active' }
//     }
//     if (typeof name === 'string'
//         && name.length > 0 && name.length < 256
//     ) {
//         if (!(/^[A-Za-z0-9_ ]*$/.test(name))){
//             return {isValid: false, msg : 'Please provide valid name for view.'}
//         }
//         let currentViewInfo = views.filter(view => view.uuid === activeView)
//         if (currentViewInfo.name === name) {
//             return { isValid: true } // if old name and new name both are same
//         }
//         let isSameNameUsed = views.filter(view => view.name === name).length
//         if (isSameNameUsed > 1) {
//             return { isValid: false, msg: 'Name already in use' }
//         }
//         return { isValid: true }
//     }
//     else {
//         if (typeof name === 'string' && name.length > 255) {
//             return { isValid: false, msg: 'Name should contain maximum of 255 characters' }
//         }
//         return { isValid: false, msg: 'Please provide valid name for view' }
//     }
// }

export function validateViewName(viewName, tables, activeView, views) {
// TODO: views are not required for this function. but as of now we are having a uuid dependency for views and tables data does not contain any uuid's.
// TODO: Write a Proper and common utility for name/alias validation of table/column/view/metadata
  if (!viewName.length || viewName.length < 3) {
    return { isValid: false, msg: "Please provide valid name for view" };
  } else if (viewName.length > 255) {
    return {
      isValid: false,
      msg: "Name should contain maximum of 255 characters",
    };
  } else if (viewName.startsWith(" ") || viewName.endsWith(" ")) {
    return { isValid: false, msg: "View name cannot start or end with space" };
  } else if (!/^[A-Za-z0-9_ ]*$/.test(viewName)) {
    return { isValid: false, msg: "Please provide valid name for view." };
  }
  //check is name/alias is not already existing in tables/views
  let validation = {isValid: true, msg: ""};
  const newTables =  Object.values(tables).map(table => {
    if(table.category === "view"){
      const tableView = views.find(view => view.id === table.id)
      return {...table, uuid: tableView.uuid}
    }
    return table;
  })
  for (const value of newTables) {
    //comparing view name because for views this name is used as keyName to render in tables
    if(value.name === viewName || value.alias === viewName) {
        if(value.uuid === activeView)
            validation = {isValid: true, msg: ""};
        else
            validation = {isValid: false, msg: "View Name already exists"};
    }       
  }
  return validation;
}
