
import React from 'react';
import { render } from '@testing-library/react';
import { handleVizShortcuts, isMatchingShortcut } from "../../components/hi-reports/utils/utilities";
import { setShotCutCurrentLocation } from '../../redux/actions';
import { eventIgnoreCondition, hiCommonShortcuts } from '../../components/common/hi-shortcuts/utils/utils';

// Mocks
const dispatchMock = jest.fn();
const commonRefsMock = {
  saveRef: { current: jest.fn() },
  undoRef: { current: jest.fn() },
  redoRef: { current: jest.fn() },
};
const eventMock = {
  ctrlKey: true,
  key: 's',
  preventDefault: jest.fn(),
};

describe("Hreport Shortcut Utils Test", () => {
  describe('isMatchingShortcut', () => {
    it('should return true when keysPressed is "Alt+o+j"', () => {
      const keysPressed = ['Alt', 'O', 'J'];
      const shortcut = ['o', 'j'];
      expect(isMatchingShortcut(keysPressed, shortcut)).toBe(true);
    });
  
    it('should return true when keysPressed is "Alt+o+c"', () => {
      const keysPressed = ['Alt', 'O', 'C'];
      const shortcut = ['o', 'c'];
      expect(isMatchingShortcut(keysPressed, shortcut)).toBe(true);
    });
  
    it('should return true when keysPressed is "Alt+o+a"', () => {
      const keysPressed = ['Alt', 'O', 'A'];
      const shortcut = ['o', 'a'];
      expect(isMatchingShortcut(keysPressed, shortcut)).toBe(true);
    });
  
    it('should return false when keysPressed is "Ctrl+o+j"', () => {
      const keysPressed = ['Ctrl', 'O', 'J'];
      const shortcut = ['o', 'j'];
      expect(isMatchingShortcut(keysPressed, shortcut)).toBe(false);
    });
  
    it('should return false when keysPressed is "Alt+j+o"', () => {
      const keysPressed = ['Alt', 'J', 'O'];
      const shortcut = ['o', 'j'];
      expect(isMatchingShortcut(keysPressed, shortcut)).toBe(false);
    });
  
    it('should return false when keysPressed is "Alt+o"', () => {
      const keysPressed = ['Alt', 'O'];
      const shortcut = ['o', 'j'];
      expect(isMatchingShortcut(keysPressed, shortcut)).toBe(false);
    });
  
    it('should return false when keysPressed is "Alt+o+j+c"', () => {
      const keysPressed = ['Alt', 'O', 'J', 'C'];
      const shortcut = ['o', 'j'];
      expect(isMatchingShortcut(keysPressed, shortcut)).toBe(false);
    });
  
    it('should return true when keysPressed is "Alt+p+d"', () => {
      const keysPressed = ['Alt', 'p', 'd'];
      const shortcut = ['p', 'd'];
      expect(isMatchingShortcut(keysPressed, shortcut)).toBe(true);
    });
    it('should return true when keysPressed is "Alt+p+r"', () => {
      const keysPressed = ['Alt', 'p', 'r'];
      const shortcut = ['p', 'r'];
      expect(isMatchingShortcut(keysPressed, shortcut)).toBe(true);
    });
    it('should return true when keysPressed is "Alt+p+x"', () => {
      const keysPressed = ['Alt', 'p', 'x'];
      const shortcut = ['p', 'x'];
      expect(isMatchingShortcut(keysPressed, shortcut)).toBe(true);
    });
    it('should return true when keysPressed is "Alt+p+i"', () => {
      const keysPressed = ['Alt', 'p', 'i'];
      const shortcut = ['p', 'i'];
      expect(isMatchingShortcut(keysPressed, shortcut)).toBe(true);
    });
    it('should return true when keysPressed is "Alt+p+i"', () => {
      const keysPressed = ['Alt', 'p', 'o'];
      const shortcut = ['p', 'o'];
      expect(isMatchingShortcut(keysPressed, shortcut)).toBe(true);
    });
    it('should return true when keysPressed is "Alt+p+m"', () => {
      const keysPressed = ['Alt', 'p', 'm'];
      const shortcut = ['p', 'm'];
      expect(isMatchingShortcut(keysPressed, shortcut)).toBe(true);
    });
  });
  describe('handleVizShortcuts', () => {
    let vizRef;
    let dispatch;
    beforeEach(() => {
      vizRef = {
        current: jest.fn(),
      };
      dispatch = jest.fn();
    });
  
    it('should call vizRef.current with "Table" when key is "t"', () => {
      handleVizShortcuts('t', vizRef, dispatch);
      expect(vizRef.current).toHaveBeenCalledWith('Table');
    });
    
    it('should call vizRef.current with "SyncChart" when key is "r"', () => {
      handleVizShortcuts('r', vizRef, dispatch);
      expect(vizRef.current).toHaveBeenCalledWith('SyncChart');
    });
  
    it('should call vizRef.current with "GridChart" when key is "g"', () => {
      handleVizShortcuts('g', vizRef, dispatch);
      expect(vizRef.current).toHaveBeenCalledWith('GridChart');
    });
  
    it('should call vizRef.current with "Antcharts" when key is "m"', () => {
      handleVizShortcuts('m', vizRef, dispatch);
      expect(vizRef.current).toHaveBeenCalledWith('Antcharts');
    });

    it('should call vizRef.current with "MapChart" when key is "x"', () => {
      handleVizShortcuts('x', vizRef, dispatch);
      expect(vizRef.current).toHaveBeenCalledWith('MapChart');
    });

    it('should call vizRef.current with "VF" when key is "v"', () => {
      handleVizShortcuts('v', vizRef, dispatch);
      expect(vizRef.current).toHaveBeenCalledWith('VF');
    });
    it('should call vizRef.current with "PivotTable" when key is "o"', () => {
      handleVizShortcuts('o', vizRef, dispatch);
      expect(vizRef.current).toHaveBeenCalledWith('S2Chart');
    });
  
    it('should not call vizRef.current or dispatch when key is not "t", "c", "r", "g", "m","x" or "v"', () => {
      handleVizShortcuts('a', vizRef, dispatch);
      expect(vizRef.current).not.toHaveBeenCalled();
      expect(dispatch).not.toHaveBeenCalled();
    });
  
    describe('setShotCutCurrentLocation in Hreport Page', () => {
    it('should not dispatch anything if Alt key is not pressed', () => {
      const dispatchMock = jest.fn()
      const keysPressed = ['Ctrl']
      setShotCutCurrentLocation(keysPressed, dispatchMock)
      expect(dispatchMock).not.toHaveBeenCalled()
    })
  
    it('should not dispatch anything if multiple keys are pressed', () => {
      const dispatchMock = jest.fn()
      const keysPressed = ['Alt', 'Ctrl']
      setShotCutCurrentLocation(keysPressed, dispatchMock)
      expect(dispatchMock).not.toHaveBeenCalled()
    })
  })
  
  
  });
  describe('eventIgnoreCondition', () => {
    test('returns true for HTMLInputElement target', () => {
      const event = { target: document.createElement('input') };
      expect(eventIgnoreCondition(event)).toBe(true);
    });
  
    test('returns true for HTMLTextAreaElement target', () => {
      const event = { target: document.createElement('textarea') };
      expect(eventIgnoreCondition(event)).toBe(true);
    });
  
    test('returns true for event with target containing "ql-editor" class', () => {
      const div = document.createElement('div');
      div.className = 'ql-editor';
      const event = { target: div };
      expect(eventIgnoreCondition(event)).toBe(true);
    });
  
    test('returns true for ctrl+f key press', () => {
      const event = { ctrlKey: true, key: 'F' };
      expect(eventIgnoreCondition(event)).toBe(true);
    });
  
    test('returns true for ctrl+c key press', () => {
      const event = { ctrlKey: true, key: 'C' };
      expect(eventIgnoreCondition(event)).toBe(true);
    });
  
    test('returns false for other events', () => {
      const event = { target: document.createElement('div'), key: 'A' };
      expect(eventIgnoreCondition(event)).toBe(undefined);
    });
  });
  describe('hiCommonShortcuts', () => {
    beforeEach(() => {
      jest.clearAllMocks();
    });

    it('should call saveRef and resetShortcuts when ctrl+s is pressed', () => {
      hiCommonShortcuts(dispatchMock, commonRefsMock, eventMock);
      expect(eventMock.preventDefault).toHaveBeenCalled();
      expect(commonRefsMock.saveRef.current).toHaveBeenCalled();
    });

    it('should call undoRef and resetShortcuts when ctrl+z is pressed', () => {
      eventMock.key = 'z';
      hiCommonShortcuts(dispatchMock, commonRefsMock, eventMock);
      expect(eventMock.preventDefault).toHaveBeenCalled();
      expect(commonRefsMock.undoRef.current).toHaveBeenCalled();
    });

    it('should call redoRef and resetShortcuts when ctrl+y is pressed', () => {
      eventMock.key = 'y';
      hiCommonShortcuts(dispatchMock, commonRefsMock, eventMock);
      expect(eventMock.preventDefault).toHaveBeenCalled();
      expect(commonRefsMock.redoRef.current).toHaveBeenCalled();
    });

    it('should call searchAndReplace and resetShortcuts when ctrl+h is pressed', () => {
      eventMock.key = 'h';
      hiCommonShortcuts(dispatchMock, commonRefsMock, eventMock);
      expect(eventMock.preventDefault).toHaveBeenCalled();
    });

  });
})

