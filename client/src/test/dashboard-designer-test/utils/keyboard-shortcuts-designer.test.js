import { designerShortcuts } from "../../../components/hi-dashboard-designer/utils/keyboard-shortcuts-designer";
import { appActions } from "../../../redux/actions";
import { toggleDashboardDrawer, updateGroupId } from "../../../redux/actions/dashboard-designer.actions";

describe("designerShortcuts", () => {
    let dispatch;
    let onSave;
    let onSaveAs;
    let refs;
    let propertyPaneRefs;
    let shortcuts;
  
    beforeEach(() => {
      dispatch = jest.fn();
      onSave = jest.fn();
      onSaveAs = jest.fn();
      refs = {
        saveDropdown: {
          current: {
            click: jest.fn(),
          },
        },
        save: {
          current: jest.fn(),
        },
        saveAs: {
          current: jest.fn(),
        },
      };
      propertyPaneRefs = {
        applyRef: {
          current: {
            click: jest.fn(),
          },
        },
        resetRef: {
          current: {
            click: jest.fn(),
          },
        },
        searchRef: {
          current: {
            click: jest.fn(),
          },
        },
      };
      shortcuts = designerShortcuts({
        dispatch,
        onSave,
        onSaveAs,
        refs,
        propertyPaneRefs,
      });
    });
  
    describe("settings", () => {
      it("should dispatch updateGroupId and toggleDashboardDrawer when onClick is called", () => {
        shortcuts.settings.onClick();
        expect(dispatch).toHaveBeenCalledWith(updateGroupId("header"));
        expect(dispatch).toHaveBeenCalledWith(toggleDashboardDrawer(true));
      });
    });
  
    describe("saveDropdown", () => {
      it("should call refs.saveDropdown.current.click and dispatch appActions.setShotCutCurrentLocation when onClick is called", () => {
        shortcuts.saveDropdown.onClick();
        expect(refs.saveDropdown.current.click).toHaveBeenCalled();
        expect(dispatch).toHaveBeenCalledWith(appActions.setShotCutCurrentLocation("DD SAVE"));
      });
    });
  
    describe("save", () => {
      it("should call refs.saveDropdown.current.click and refs.save.current when onClick is called", () => {
        shortcuts.save.onClick();
        expect(refs.saveDropdown.current.click).toHaveBeenCalled();
        expect(refs.save.current).toHaveBeenCalled();
      });
    });
  
    describe("saveAs", () => {
      it("should call refs.saveDropdown.current.click and refs.saveAs.current when onClick is called", () => {
        shortcuts.saveAs.onClick();
        expect(refs.saveDropdown.current.click).toHaveBeenCalled();
        expect(refs.saveAs.current).toHaveBeenCalled();
      });
    });
  
  });
  