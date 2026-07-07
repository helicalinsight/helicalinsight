//this function is not used anywhere in the application.
export const updateViewWithLabels = ({ views, activeView, res }) => {
    console.log('in update views with labels', { views, activeView, res })
    res.labels = res.labels.map(option => ({ ...option, checked: true }))
    views = views.map(eachView => {
        if (eachView.uuid === activeView) {
            eachView = { ...eachView, ...res }
        }
        return eachView
    })
    return views
}