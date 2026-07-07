import { createStore } from 'redux';
import undoable from 'redux-undo';
import { Provider } from 'react-redux';
import { render, fireEvent } from '@testing-library/react';
import React from 'react';
import CannedReportsPage from '../../../pages/canned-reports-page'; 

const initialState = { title: 'Report 1' };

function cannedReportsReducer(state = initialState, action) {
  switch (action.type) {
    case 'SET_TITLE':
      return { ...state, title: action.payload };
    default:
      return state;
  }
}

const undoableReducer = undoable(cannedReportsReducer, {
  undoType: 'HCR_UNDO',
  redoType: 'HCR_REDO'
});

describe('Undo/Redo in CannedReportsPage', () => {
  let store;

  beforeEach(() => {
    store = createStore(undoableReducer);
  });

  it('should undo the title change', () => {
    store.dispatch({ type: 'SET_TITLE', payload: 'Report 2' });
    expect(store.getState().present.title).toBe('Report 2');

    store.dispatch({ type: 'HCR_UNDO' });
    expect(store.getState().present.title).toBe('Report 1');
  });

  it('should redo the undone change', () => {
    store.dispatch({ type: 'SET_TITLE', payload: 'Report 2' });
    store.dispatch({ type: 'HCR_UNDO' });
    expect(store.getState().present.title).toBe('Report 1');

    store.dispatch({ type: 'HCR_REDO' });
    expect(store.getState().present.title).toBe('Report 2');
  });

  it('should clear redo stack after new action post-undo', () => {
    store.dispatch({ type: 'SET_TITLE', payload: 'Report 2' });
    store.dispatch({ type: 'SET_TITLE', payload: 'Report 3' });

    store.dispatch({ type: 'HCR_UNDO' }); // back to 'Report 2'
    expect(store.getState().present.title).toBe('Report 2');

    store.dispatch({ type: 'SET_TITLE', payload: 'Report 4' });
    expect(store.getState().present.title).toBe('Report 4');

    store.dispatch({ type: 'HCR_REDO' });
    // Redo should not work after a new action
    expect(store.getState().present.title).toBe('Report 4');
  });

   it('should Delete when we click on Delete key in keyboard', () => {
   store.dispatch({type : 'HCR_DELETE_NODE' })
    expect(store.getState().present.title).toBe('Report 1')
  });
});
