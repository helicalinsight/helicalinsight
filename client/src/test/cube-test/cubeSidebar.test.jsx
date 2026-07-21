import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { CubeSidebar } from "../../components/hi-cube/cubeSidebar";
import { HIFileBrowser } from "../../components";
import { ShareFinalModal } from "../../components/hi-fileBrowser/components";
import hiMetadataArea from "../../components/hi-sidebar/hr-hreportSidebar/hi-metadata-area";
import { SidebarFooter } from "../../components";
import { CubeMetadata } from "../../components/hi-cube/cubeMetadata";
import { CustomIcon } from "../../components/common/custom-icons/CustomIcon";

const mockDispatch = jest.fn();

jest.mock("react-redux", () => ({
    useDispatch: () => mockDispatch,
    useSelector: jest.fn(),
}));

jest.mock("../../base/requests/cube.requests", () =>
    jest.fn(() => ({
        getCubeMetadataTablesData: jest.fn(),
    }))
);

jest.mock("../../redux/actions/cube.actions", () => ({
    saveCubeMetadataFileDetails: jest.fn((payload) => ({
        type: "SAVE_CUBE_METADATA_FILE_DETAILS",
        payload,
    })),
    setCubeFieldsData: jest.fn((payload) => ({
        type: "SET_CUBE_FIELDS_DATA",
        payload,
    })),
    setCubeMode: jest.fn((mode) => ({ type: "SET_CUBE_MODE", payload: mode })),
    savedCubeDetailsForEdit: jest.fn((payload) => ({
        type: "SAVE_CUBE_DETAILS_FOR_EDIT",
        payload,
    })),
    cubeFileDataAfterSave: jest.fn((payload) => ({
        type: "CUBE_DATA_AFTER_SAVE",
        payload,
    })),
}));

jest.mock("../../redux/actions", () => ({
    appActions: {
        setEditModeInfo: jest.fn((payload) => ({
            type: "SET_EDIT_MODE_INFO",
            payload,
        })),
    },
    fileBrowserActions: {
        setShowFileBrowser: jest.fn((val) => ({
            type: "SET_SHOW_FILE_BROWSER",
            payload: val,
        })),
    },
}));

jest.mock("./helperMethods", () => ({
    fbOnClickHandler: jest.fn(),
    saveCubeSelectedMetadataFileDetails: jest.fn(),
}));

jest.mock("../../components", () => ({
    HIFileBrowser: ({ onDoubleClick }) => (
        <div data-testid="hi-file-browser">
            <button
                data-testid="fb-double-click-btn"
                onClick={() =>
                    onDoubleClick({
                        path: "/some/path/file.metadata",
                        name: "file.metadata",
                        extension: "metadata",
                        title: "File Title",
                    })
                }
            >
                Double click file
            </button>
        </div>
    ),
    SidebarFooter: () => <div data-testid="sidebar-footer" />,
}));

jest.mock("../../components/hi-fileBrowser/components", () => ({
    ShareFinalModal: ({ shareOptions }) => (
        <div data-testid="share-final-modal">{shareOptions.file}</div>
    ),
}));

jest.mock("../../components/hi-sidebar/hr-hreportSidebar/hi-metadata-area", () => ({
    __esModule: true,
    default: ({ openFileBrowser, onConnectToMetadata }) => (
        <div data-testid="hi-metadata-area">
            <button data-testid="open-file-browser-btn" onClick={openFileBrowser}>
                Open file browser
            </button>
            <button data-testid="connect-to-metadata-btn" onClick={onConnectToMetadata}>
                Connect to metadata
            </button>
        </div>
    ),
}));

jest.mock("../../components/common/custom-icons/CustomIcon", () => ({
    CustomIcon: ({ name }) => <span data-testid={`icon-${name}`}>{name}</span>,
}));

jest.mock("../../components/hi-cube/cubeMetadata", () => ({
    CubeMetadata: () => <div data-testid="cube-metadata" />,
}));

const { useSelector } = require("react-redux");
const { fbOnClickHandler } = require("./helperMethods");
const {
    saveCubeMetadataFileDetails,
    setCubeFieldsData,
    setCubeMode,
    savedCubeDetailsForEdit,
    cubeFileDataAfterSave,
} = require("../../redux/actions/cube.actions");
const { fileBrowserActions, appActions } = require("../../redux/actions");

const buildDefaultSelectorState = (overrides = {}) => ({
    app: {
        applicationSettingsData: {
            userData: {
                user: { roles: ["admin"] },
            },
        },
        toggleSidebar: false,
    },
    cube: {
        cubeMode: "create",
        cubeDataAfterSave: { dir: "/save/dir", file: "cube.hrcube" },
        metadataTablesData: [],
        metadataDetails: { fileName: "", path: "" },
    },
    fileBrowser: {
        globalFbEnabled: false,
        showFileBrowser: false,
        isShareModalVisible: false,
    },
    ...overrides,
});

const buildSelectorMap = (state) => (selector) => {
    const fullState = {
        app: state.app,
        cube: state.cube,
        fileBrowser: state.fileBrowser,
    };
    return selector(fullState);
};

const defaultProps = {
    shareRef: { current: false },
    fbProperties: {},
    filebrowserFor: "",
    setFilebrowserFor: jest.fn(),
    customClass: "",
};

const renderSidebar = (propsOverride = {}, stateOverride = {}) => {
    const state = buildDefaultSelectorState(stateOverride);
    useSelector.mockImplementation(buildSelectorMap(state));
    return render(<CubeSidebar {...defaultProps} {...propsOverride} />);
};

const resetCubeRequestsMock = () => {
    const cubeRequests = require("../../base/requests/cube.requests");
    cubeRequests.mockImplementation(() => ({
        getCubeMetadataTablesData: jest.fn(),
    }));
};

beforeEach(() => {
    jest.clearAllMocks();
    resetCubeRequestsMock();
});

describe("CubeSidebar", () => {
    describe("rendering", () => {
        it("it should renders the metadata area", () => {
            renderSidebar();
            expect(screen.getByTestId("hi-metadata-area")).toBeInTheDocument();
        });

        it("should does not render HIFilebrowser when globalFbEnabled is false", () => {
            renderSidebar();
            expect(screen.queryByTestId("hi-file-browser")).not.toBeInTheDocument();
        });

        it("should renders sharefinalmodal when shareRef.current is true & isShareModalVisible is true", () => {
            renderSidebar(
                { shareRef: { current: true } },
                { fileBrowser: { globalFbEnabled: false, showFileBrowser: false, isShareModalVisible: true } }
            );
            expect(screen.getByTestId("share-final-modal")).toBeInTheDocument();
        });

        it("it shoudl does not render sharefinalmodal when shareref.current is false", () => {
            renderSidebar({ shareRef: { current: false } });
            expect(screen.queryByTestId("share-final-modal")).not.toBeInTheDocument();
        });
    });

    describe("openFileBrowser", () => {
        it("it should shows confirmation modal when metadata is already selected", () => {
            renderSidebar(
                {},
                {
                    cube: {
                        cubeMode: "edit",
                        cubeDataAfterSave: {},
                        metadataTablesData: [],
                        metadataDetails: { fileName: "Metadata_1.metadata", path: "/some/path" },
                    },
                }
            );
            fireEvent.click(screen.getByTestId("open-file-browser-btn"));
            expect(screen.getByText("Open another metadata file?")).toBeInTheDocument();
        });
    });

    // -----------------------------------------------------------------------
    describe("onConnectToMetadata", () => {
        it("should shows confirmation modal when metadata is already selected", () => {
            renderSidebar(
                {},
                {
                    cube: {
                        cubeMode: "edit",
                        cubeDataAfterSave: {},
                        metadataTablesData: [],
                        metadataDetails: { fileName: "Metadata_1.metadata", path: "/some/path" },
                    },
                }
            );
            fireEvent.click(screen.getByTestId("connect-to-metadata-btn"));
            expect(screen.getByText("Open another metadata file?")).toBeInTheDocument();
        });
    });

    describe("urlObj prop", () => {
        it("it should dispatches saveCubeMetadataFileDetails when urlObj has dir and .metadata file", () => {
            renderSidebar({
                urlObj: { dir: "/url/path", file: "FromUrl.metadata" },
            });
            expect(saveCubeMetadataFileDetails).toHaveBeenCalledWith({
                path: "/url/path",
                fileName: "FromUrl.metadata",
            });
        });

        it("should does not dispatch when urlObj file extension is not metadata", () => {
            renderSidebar({
                urlObj: { dir: "/url/path", file: "someFile.hrcube" },
            });
            expect(saveCubeMetadataFileDetails).not.toHaveBeenCalled();
        });

        it("it should does not dispatch when urlObj is empty", () => {
            renderSidebar({ urlObj: {} });
            expect(saveCubeMetadataFileDetails).not.toHaveBeenCalled();
        });
    });

    describe("getCubeMetadataTablesData effect", () => {
        afterEach(() => {
            resetCubeRequestsMock();
        });

        it("should calls getCubeMetadataTablesData when path and fileName are present", () => {
            const getCubeMetadataTablesData = jest.fn();
            const cubeRequests = require("../../base/requests/cube.requests");
            cubeRequests.mockImplementation(() => ({ getCubeMetadataTablesData }));

            renderSidebar(
                {},
                {
                    cube: {
                        cubeMode: "create",
                        cubeDataAfterSave: {},
                        metadataTablesData: [],
                        metadataDetails: { fileName: "Metadata_1.metadata", path: "/some/path" },
                    },
                }
            );
            expect(getCubeMetadataTablesData).toHaveBeenCalledWith({
                path: "/some/path",
                fileName: "Metadata_1.metadata",
            });
        });

        it("should does not call getCubeMetadataTablesData when path or fileName is empty", () => {
            const getCubeMetadataTablesData = jest.fn()
            const cubeRequests = require("../../base/requests/cube.requests")
            cubeRequests.mockImplementation(() => ({ getCubeMetadataTablesData }));
            renderSidebar();
            expect(getCubeMetadataTablesData).not.toHaveBeenCalled();
        });
    });
});