import undoable from 'redux-undo';

const ADD_TAB = 'ADD_TAB';
const UNDO = 'UNDO';
const REDO = 'REDO';

const tabReducer = (state = [], action) => {
  switch (action.type) {
    case ADD_TAB:
      return [...state, action.payload];
    default:
      return state;
  }
};

const undoableReducer = undoable(tabReducer, {
  undoType: UNDO,
  redoType: REDO,
});

describe('Testing the undo/redo', () => {
  it('can undo and redo a tab add', () => {
    let state = undoableReducer(undefined, { type: '@@INIT' });

    state = undoableReducer(state, {
      type: ADD_TAB,
      payload: { id: '1', title: 'Tab 1' },
    });

    expect(state.present).toHaveLength(1);

    // Undo
    state = undoableReducer(state, { type: UNDO });
    expect(state.present).toHaveLength(0); 

    // Redo
    state = undoableReducer(state, { type: REDO });
    expect(state.present).toHaveLength(1); 
    expect(state.present[0].id).toBe('1');
  });
});
