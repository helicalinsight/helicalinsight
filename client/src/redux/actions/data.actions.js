
export const SET_INIT_DATA  = "SET_INIT_DATA";
export const ON_NEXT_VIEW  = "ON_NEXT_VIEW";

export const setInitData = (formdata)=>{
    return { 
        data:JSON.parse(`
        [
            {"slno":"1","id":4,"name":"downloadManager","email":"download@helicalinsight.com","enabled":true,"organisation":"","orgName":"Null","roles":[{"id":2,"role":"ROLE_USER"},{"id":4,"role":"ROLE_DOWNLOAD"}],"profiles":[]},
            {"slno":"2","id":1,"name":"hiadmin","email":"admin@helicalinsight.com","enabled":true,"organisation":"","orgName":"Null","roles":[{"id":1,"role":"ROLE_ADMIN"},{"id":2,"role":"ROLE_USER"},{"id":3,"role":"ROLE_VIEWER"}],"profiles":[]},
            {"slno":"3","id":2,"name":"hiuser","email":"user@helicalinsight.com","enabled":true,"organisation":"","orgName":"Null","roles":[{"id":2,"role":"ROLE_USER"}],"profiles":[]},
            {"slno":"4","id":3,"name":"hiviewer","email":"viewer@helicalinsight.com","enabled":true,"organisation":"","orgName":"Null","roles":[{"id":3,"role":"ROLE_VIEWER"}],"profiles":[]}]
        `),
        viewPage:1,
        currentPage:1,
        viewEntries:3,
        pageSize:1000,
        fetchMore:true,
        showMore:true ,
        key:formdata.key, 
        type:SET_INIT_DATA
    }
}

export const onNextView = (formdata) => {
    return{
        type:ON_NEXT_VIEW,
        key:formdata.key, 
    }
}