export const checkIfGroovyManaged = (data) => {
    return data?.type === 'sql.jdbc.groovy.managed'
}