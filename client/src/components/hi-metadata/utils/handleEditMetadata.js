import { getMetadata } from '.'

export const handleEditMetadata = ({ location, uuid, store, dispatch, returnFetched }) => {
    getMetadata({ location, uuid, store, dispatch, returnFetched })
}