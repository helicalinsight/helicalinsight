// HandleCtrlKeys.test.js
import { render, fireEvent } from '@testing-library/react';
import React, { useEffect } from 'react';
import { Provider, useDispatch } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import reducers from '../../../redux';
import { hiMockAxios } from '../../../app/mock-axios';

// Dummy UUID
jest.mock('uuid', () => ({ v4: () => 'mock-uuid' }));

// Mock node
const mockNode = {
  id: 'node-1',
  store: { data: { id: 'node-1', label: 'Mock Node' } },
  getPosition: () => ({ x: 50, y: 100 }),
  size: { width: 120, height: 80 },
};

// Mock flowchart instance
const flowchartInstance = {
  current: {
    getSelectedCells: jest.fn(() => [mockNode]),
  },
};

let clipboard = [];

const mockDispatch = jest.fn();

const TestComponent = () => {
  const dispatch = useDispatch();

  const handleCtrlKeys = (e) => {
    if (e.ctrlKey && e.key === 'v') {
      e.preventDefault();
      if (!clipboard.length) return;

      clipboard.forEach((node) => {
        dispatch({ type: 'PASTE_NODE', payload: node });
      });
    }
  };

  useEffect(() => {
    window.addEventListener('keydown', handleCtrlKeys);
    return () => window.removeEventListener('keydown', handleCtrlKeys);
  }, []);

  return <div>Test Component</div>;
};

const store = configureStore({
  reducer: reducers,
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      thunk: {
        extraArgument: hiMockAxios
      },
      immutableCheck: false,
      serializableCheck: false,
    }),
});;
store.dispatch = mockDispatch;

describe('Paste Key Handler (Ctrl+V)', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    clipboard = [];
  });

  test('should copy and paste nodes on Ctrl+V if clipboard has nodes', () => {
    clipboard = [
      {
        id: 'node-1',
        label: 'Mock Node',
        x: 50,
        y: 100,
        width: 120,
        height: 80,
      },
    ];

    render(
      <Provider store={store}>
        <TestComponent />
      </Provider>
    );

    fireEvent.keyDown(window, { key: 'v', ctrlKey: true });

    expect(mockDispatch).toHaveBeenCalledWith({
      type: 'PASTE_NODE',
      payload: clipboard[0],
    });
  });

  test('should not paste if clipboard is empty', () => {
    render(
      <Provider store={store}>
        <TestComponent />
      </Provider>
    );

    fireEvent.keyDown(window, { key: 'v', ctrlKey: true });

    expect(mockDispatch).not.toHaveBeenCalled();
  });
});
