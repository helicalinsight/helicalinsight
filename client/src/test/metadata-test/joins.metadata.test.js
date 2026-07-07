import { 
    storeSnapShotForJoins
} from './constants'
import { handleJoinActions } from '../../components/hi-metadata/utils/handleJoinActions'
/**
 * delete
 * delete selected joins
 * sort - there isn't much logic - it is handled by component mostly
 * discard column
 * add new join
 * validate joins
 * delete invalid joins
 * check empty joins
 */

let store = {
    getState: () => ({
        metadata: storeSnapShotForJoins
    })
}

describe('Deleting joins one by one', () => {
    test('valid args - checking delete property in action', () => {
        let joinsToDelete = store.getState().metadata.joins[0]
        let result = handleJoinActions({
            action: "discardJoin",
            dispatch: false,
            joins: store.getState().metadata.joins,
            record: joinsToDelete,
            returnDataForJest: true
        })
        let isDeleted = false
        result.map(join => {
            if (join.id === joinsToDelete.id && join.action === 'delete') {
                isDeleted = true
            }
        })
        let noOfJoinsDeleted = result.filter(join => join.action === 'delete')
        expect(isDeleted).toBe(true)
    })

    test('valid args - checking count of deleted joins', () => {
        let joinsToDelete = store.getState().metadata.joins[0]
        let result = handleJoinActions({
            action: "discardJoin",
            dispatch: false,
            joins: store.getState().metadata.joins,
            record: joinsToDelete,
            returnDataForJest: true
        })
        let isDeleted = false
        result.map(join => {
            if (join.id === joinsToDelete.id && join.action === 'delete') {
                isDeleted = true
            }
        })
        let noOfJoinsDeleted = result.filter(join => join.action === 'delete')
        expect(noOfJoinsDeleted.length).toBe(1)
    })

    test('valid args - deleting multiple joins and checkng for count joins', () => {
        let joinsToDelete = store.getState().metadata.joins[0]
        let result = handleJoinActions({
            action: "discardJoin",
            dispatch: false,
            joins: store.getState().metadata.joins,
            record: joinsToDelete,
            returnDataForJest: true
        })
        result = handleJoinActions({
            action: "discardJoin",
            dispatch: false,
            joins: result,
            record: result[1],
            returnDataForJest: true
        })
        let noOfJoinsDeleted = result.filter(join => join.action === 'delete')
        expect(noOfJoinsDeleted.length).toBe(2)
    })
})


describe('Deleting joins one by one in metadata', () => {
    test('valid args - checking no of joins deleted', () => {
        let result = handleJoinActions({
            action: "discardSelectedJoins",
            dispatch: false,
            joins: store.getState().metadata.joins,
            record: [],
            returnDataForJest: true,
            listOfTableNames: Object.values(store.getState().metadata.tables).map(table => table.name),
            selectedJoins: [store.getState().metadata.joins[0].uuid, store.getState().metadata.joins[1].uuid]
        })
        let joinsDeleted = result.map(join => (join.action === 'delete' ? join : false)).filter(Boolean)
        expect(joinsDeleted.length).toBe(2)
    })

    test('valid args - checking no of joins deleted - deleting 3 selected joins', () => {
        let result = handleJoinActions({
            action: "discardSelectedJoins",
            dispatch: false,
            joins: store.getState().metadata.joins,
            record: [],
            returnDataForJest: true,
            listOfTableNames: Object.values(store.getState().metadata.tables).map(table => table.name),
            selectedJoins: [store.getState().metadata.joins[0].uuid, store.getState().metadata.joins[1].uuid, store.getState().metadata.joins[2].uuid]
        })
        let joinsDeleted = result.map(join => (join.action === 'delete' ? join : false)).filter(Boolean)
        expect(joinsDeleted.length).toBe(3)
    })

    test('valid args - checking no of joins deleted - validating if selected joins are none', () => {
        let result = handleJoinActions({
            action: "discardSelectedJoins",
            dispatch: () => {},
            joins: store.getState().metadata.joins,
            record: [],
            returnDataForJest: true,
            listOfTableNames: Object.values(store.getState().metadata.tables).map(table => table.name),
            selectedJoins: []
        })
        let joinsDeleted = result.map(join => (join.action === 'delete' ? join : false)).filter(Boolean)
        expect(joinsDeleted.length).toBe(0)
    })

    test('valid args - checking no of joins deleted - validating if joins are empty', () => {
        let result = handleJoinActions({
            action: "discardSelectedJoins",
            dispatch: false,
            joins: [],
            record: [],
            returnDataForJest: true,
            listOfTableNames: Object.values(store.getState().metadata.tables).map(table => table.name),
            selectedJoins: [store.getState().metadata.joins[0].uuid, store.getState().metadata.joins[1].uuid, store.getState().metadata.joins[2].uuid]
        })
        let joinsDeleted = result.map(join => (join.action === 'delete' ? join : false)).filter(Boolean)
        expect(joinsDeleted.length).toBe(0)
    })

    test('valid args - checking no of joins deleted - validating if joins are not of type array', () => {
        let result = handleJoinActions({
            action: "discardSelectedJoins",
            dispatch: false,
            joins: null,
            record: [],
            returnDataForJest: true,
            listOfTableNames: Object.values(store.getState().metadata.tables).map(table => table.name),
            selectedJoins: [store.getState().metadata.joins[0].uuid, store.getState().metadata.joins[1].uuid, store.getState().metadata.joins[2].uuid]
        })

        expect(result).toBe(false)
    })
})



describe('Testing discarding columns', () => {
    test('left discard colum for one join - checking for left column', () => {
        let result = handleJoinActions({
            action: "discardColumn",
            dispatch: false,
            joins: store.getState().metadata.joins,
            record: store.getState().metadata.joins[0],
            returnDataForJest: true,
            type: 'left'
        })
        let currentJoin = store.getState().metadata.joins[0]
        let isValid = true
        let discardedJoin = result.filter(join => join.uuid === currentJoin.uuid)[0]
        if (!(discardedJoin.left.table === false && discardedJoin.left.column === false)) {
            isValid = false
        }
        expect(isValid).toBe(true)
    })

    test('left discard colum for one join - checking for right column', () => {
        let result = handleJoinActions({
            action: "discardColumn",
            dispatch: false,
            joins: store.getState().metadata.joins,
            record: store.getState().metadata.joins[0],
            returnDataForJest: true,
            type: 'left'
        })
        let currentJoin = store.getState().metadata.joins[0]
        let isValid = true
        let discardedJoin = result.filter(join => join.uuid === currentJoin.uuid)[0]
        if (!(discardedJoin.right.table !== false && discardedJoin.right.column !== false)) {
            isValid = false
        }
        expect(isValid).toBe(true)
    })

    test('right discard colum for one join - checking for left column', () => {
        let result = handleJoinActions({
            action: "discardColumn",
            dispatch: false,
            joins: store.getState().metadata.joins,
            record: store.getState().metadata.joins[0],
            returnDataForJest: true,
            type: 'right'
        })
        let currentJoin = store.getState().metadata.joins[0]
        let isValid = true
        let discardedJoin = result.filter(join => join.uuid === currentJoin.uuid)[0]
        if (!(discardedJoin.left.table !== false && discardedJoin.left.column !== false)) {
            isValid = false
        }
        expect(isValid).toBe(true)
    })

    test('right discard colum for one join - checking for right column', () => {
        let result = handleJoinActions({
            action: "discardColumn",
            dispatch: false,
            joins: store.getState().metadata.joins,
            record: store.getState().metadata.joins[0],
            returnDataForJest: true,
            type: 'right'
        })
        let currentJoin = store.getState().metadata.joins[0]
        let isValid = true
        let discardedJoin = result.filter(join => join.uuid === currentJoin.uuid)[0]
        if (!(discardedJoin.left.table !== false && discardedJoin.left.column !== false)) {
            isValid = false
        }
        expect(isValid).toBe(true)
    })

    test('right discard colum for one join - checking other joins for impacts', () => {
        let result = handleJoinActions({
            action: "discardColumn",
            dispatch: false,
            joins: store.getState().metadata.joins,
            record: store.getState().metadata.joins[0],
            returnDataForJest: true,
            type: 'right'
        })
        let currentJoin = store.getState().metadata.joins[0]
        let isValid = true
        let discardedJoin = result.filter(join => join.uuid !== currentJoin.uuid)
        discardedJoin.map(join => {
            if (join.left.table === false || join.left.column === false || join.right.table === false || join.right.column === false) {
                isValid = false
            }
        })
        expect(isValid).toBe(true)
    })
})



describe('adding new joins', () => {
    test('add new joins, checking length', () => {
        let result = handleJoinActions({
            action: "addNewJoin",
            dispatch: false,
            joins: store.getState().metadata.joins,
            record: store.getState().metadata.joins[0],
            returnDataForJest: true,
            type: 'right'
        })
        expect(result.length).toBe(6)
    })

    test('add new joins, checking newly added join', () => {
        let result = handleJoinActions({
            action: "addNewJoin",
            dispatch: false,
            joins: store.getState().metadata.joins,
            record: store.getState().metadata.joins[0],
            returnDataForJest: true,
            type: 'right'
        })
        let newJoin = result[0]
        let isValid = true
        if (!(newJoin.left.table === false && newJoin.right.table === false && newJoin.left.column === false && newJoin.right.column === false)) {
            isValid = false
        }
        expect(isValid).toBe(true)
    })

    test('add two new joins at same time, checking newly added join', () => {
        let result = handleJoinActions({
            action: "addNewJoin",
            dispatch: false,
            joins: store.getState().metadata.joins,
            record: store.getState().metadata.joins[0],
            returnDataForJest: true,
            type: 'right'
        })
        result = handleJoinActions({
            action: "addNewJoin",
            dispatch: false,
            joins: result,
            record: store.getState().metadata.joins[0],
            returnDataForJest: true,
            type: 'right'
        })
        expect(result).toBeFalsy()
    })
})

// validating joins

// describe('validating joins and checking empty joins', () => {
//     test('validating joins - all valid', () => {
//         let result = handleJoinActions({
//             action: "validateJoins",
//             dispatch: false,
//             joins: store.getState().metadata.joins,
//             returnDataForJest: true,
//             listOfTableNames: Object.values(store.getState().metadata.tables).map(table => table.name)
//         })
//         expect(!result.emptyJoins && !result.invalidJoins).toBe(true)
//     })

//     // test('validating joins - with one empty join', () => {
//     //     let result = handleJoinActions({
//     //         action: "addNewJoin",
//     //         dispatch: false,
//     //         joins: store.getState().metadata.joins,
//     //         record: store.getState().metadata.joins[0],
//     //         returnDataForJest: true,
//     //         type: 'right'
//     //     })
//     //     result = handleJoinActions({
//     //         action: "validateJoins",
//     //         dispatch: false,
//     //         joins: result,
//     //         returnDataForJest: true,
//     //         listOfTableNames: Object.values(store.getState().metadata.tables).map(table => table.name)
//     //     })
//     //     expect(result.emptyJoins && result.invalidJoins).toBe(true)
//     // })

//     // test('validating joins - testing invalid joins', () => {
//     //     let result = handleJoinActions({
//     //         action: "validateJoins",
//     //         dispatch: false,
//     //         joins: store.getState().metadata.joins,
//     //         returnDataForJest: true,
//     //         listOfTableNames: [Object.values(store.getState().metadata.tables).map(table => table.name)[0]]
//     //     })
//     //     expect(!result.emptyJoins && result.invalidJoins).toBe(true)
//     // })
// })

// describe('Delete invalid joins', () => {
//     test('delete invalid joins valid case', () => {
//         let result = handleJoinActions({
//             action: "discardColumn",
//             dispatch: false,
//             joins: store.getState().metadata.joins,
//             record: store.getState().metadata.joins[0],
//             returnDataForJest: true,
//             type: 'left'
//         })
//         result = handleJoinActions({
//             action: "deleteInvalid",
//             dispatch: false,
//             joins: result,
//             returnDataForJest: true,
//             listOfTableNames: [Object.values(store.getState().metadata.tables).map(table => table.name)[0]]
//         })
//         let firstJoin = result[0]
//         expect(result[0].action).toBe('delete')
//     })
//     test('delete invalid joins when there are no invliad cases', () => {
//         let result = handleJoinActions({
//             action: "deleteInvalid",
//             dispatch: false,
//             joins: store.getState().metadata.joins,
//             returnDataForJest: true,
//             listOfTableNames: Object.values(store.getState().metadata.tables).map(table => table.name)
//         })
//         let firstJoin = result[0]
//         let isValid = true
//         result.map(join => {
//             if(join.action === 'delete') {
//                 isValid = false
//             }
//         })
//         expect(isValid).toBe(true)
//     })

// })