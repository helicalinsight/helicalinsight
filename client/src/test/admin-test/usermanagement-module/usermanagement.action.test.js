import { configureStore } from "@reduxjs/toolkit";
import reducers from "../../../redux/index";
import actionTypes from "../../../redux/actions/actionTypes";
import mocks from "./admin-data-mock";
import { v4 as uuidv4 } from "uuid";

const { admin_initial_view_state } = mocks;

describe("User Management Actions", () => {
  const store = configureStore({
    reducer: reducers,
    middleware: (getDefaultMiddleware) =>
      getDefaultMiddleware({
        thunk: {},
        immutableCheck: false,
        serializableCheck: false,
      }),
  });
  let dispatch = store.dispatch;
  test("User Data", (done) => {
    dispatch({
      type: actionTypes.STORE_USER_DATA,
      payload: admin_initial_view_state.userData,
    });
    expect(store.getState().admin.userData).toEqual(
      admin_initial_view_state.userData
    );
    done();
  });
  test("Org Data", (done) => {
    dispatch({
      type: actionTypes.STORE_ORG_DATA,
      payload: admin_initial_view_state.orgData,
    });
    expect(store.getState().admin.orgData).toEqual(
      admin_initial_view_state.orgData
    );
    done();
  });
  test("Role Data", (done) => {
    dispatch({
      type: actionTypes.STORE_ROLE_DATA,
      payload: admin_initial_view_state.roleData,
    });
    expect(store.getState().admin.roleData).toEqual(
      admin_initial_view_state.roleData
    );
    done();
  });
  test("Update Visible Drawers", (done) => {
    dispatch({
      type: actionTypes.UPDATE_VISIBLE_DRAWERS,
      payload: { key: "editUser", status: true },
    });
    expect(store.getState().admin.visibleDrawersUM.editUser).toEqual(true);
    done();
  });
  test("Update Edit User", (done) => {
    let id = uuidv4();
    dispatch({
      type: actionTypes.UPDATE_EDIT_USER,
      payload: { type: "user", userId: id },
    });
    expect(store.getState().admin.editUser).toEqual({
      type: "user",
      userId: id,
    });
    done();
  });
  test("Delete Org Item", (done) => {
    dispatch({
      type: actionTypes.DELETE_ORG_ITEM,
      payload: 1,
    });
    expect(store.getState().admin.orgData).toEqual([]);
    done();
  });
  test("Delete Role Item", (done) => {
    dispatch({
      type: actionTypes.DELETE_ROLE_ITEM,
      payload: 1,
    });
    expect(store.getState().admin.roleData).toEqual([
      {
        id: 102,
        name: "ROLE_USER",
        organisation: 1,
        orgName: "adsfasdf",
        slno: "1",
      },
      {
        id: 101,
        name: "ROLE_ADMIN",
        organisation: 1,
        orgName: "adsfasdf",
        slno: "2",
      },
      {
        id: 4,
        name: "ROLE_DOWNLOAD",
        organisation: "",
        orgName: "Null",
        slno: "3",
      },
      {
        id: 3,
        name: "ROLE_VIEWER",
        organisation: "",
        orgName: "Null",
        slno: "4",
      },
      {
        id: 2,
        name: "ROLE_USER",
        organisation: "",
        orgName: "Null",
        slno: "5",
      },
    ]);
    done();
  });
  test("Delete User Item", (done) => {
    dispatch({
      type: actionTypes.DELETE_USER_ITEM,
      payload: 1,
    });
    expect(store.getState().admin.userData).toEqual([
      {
        id: 4,
        name: "downloadManager",
        email: "mailto:download@helicalinsight.com",
        enabled: true,
        organisation: "",
        orgName: "Null",
        roles: [
          {
            id: 2,
            role: "ROLE_USER",
          },
          {
            id: 4,
            role: "ROLE_DOWNLOAD",
          },
        ],
        profiles: [],
        slno: "1",
      },

      {
        id: 2,
        name: "hiuser",
        email: "mailto:user@helicalinsight.com",
        enabled: true,
        organisation: "",
        orgName: "Null",
        roles: [
          {
            id: 2,
            role: "ROLE_USER",
          },
        ],
        profiles: [],
        slno: "3",
      },
      {
        id: 3,
        name: "hiviewer",
        email: "mailto:viewer@helicalinsight.com",
        enabled: true,
        organisation: "",
        orgName: "Null",
        roles: [
          {
            id: 3,
            role: "ROLE_VIEWER",
          },
        ],
        profiles: [],
        slno: "4",
      },
    ]);
    done();
  });
  test("Delete Profile Item", (done) => {
    dispatch({
      type: actionTypes.DELETE_PROFILE_ITEM,
      payload: { userId: 4, profileId: 1 },
    });
    expect(
      store.getState().admin.userData.find((item) => item.id === 4).profiles
    ).toEqual([]);
    done();
  });
  test("Add Profile Item", (done) => {
    dispatch({
      type: actionTypes.ADD_PROFILE_ITEM,
      payload: {
        userId: 4,
        profileItem: {
          id: 1,
          name: "asdfsadf",
          value: "sadfsdaf",
        },
      },
    });
    expect(
      store.getState().admin.userData.find((item) => item.id === 4).profiles
    ).toEqual([
      {
        id: 1,
        name: "asdfsadf",
        value: "sadfsdaf",
      },
    ]);
    done();
  });
  test("Add Org Item", (done) => {
    dispatch({
      type: actionTypes.ADD_ORG_ITEM,
      payload: {
        slno: admin_initial_view_state.orgData.length + 1,
        ...{
          id: 2,
          name: "sdafsdaf",
          description: "adsfadsfdsa",
        },
      },
    });
    expect(store.getState().admin.orgData).toEqual([
      {
        description: "adsfadsfdsa",
        id: 2,
        name: "sdafsdaf",
        slno: 2,
      },
    ]);
    done();
  });
  test("Add Role Item", (done) => {
    dispatch({
      type: actionTypes.ADD_ROLE_ITEM,
      payload: {
        slno: admin_initial_view_state.roleData.length + 1,
        ...{
          id: 105,
          name: "adsfsdaf",
          organisation: 1,
          orgName: "asdf",
        },
      },
    });
    expect(store.getState().admin.roleData).toEqual([
      {
        id: 102,
        name: "ROLE_USER",
        organisation: 1,
        orgName: "adsfasdf",
        slno: "1",
      },
      {
        id: 101,
        name: "ROLE_ADMIN",
        organisation: 1,
        orgName: "adsfasdf",
        slno: "2",
      },
      {
        id: 4,
        name: "ROLE_DOWNLOAD",
        organisation: "",
        orgName: "Null",
        slno: "3",
      },
      {
        id: 3,
        name: "ROLE_VIEWER",
        organisation: "",
        orgName: "Null",
        slno: "4",
      },
      {
        id: 2,
        name: "ROLE_USER",
        organisation: "",
        orgName: "Null",
        slno: "5",
      },

      {
        slno: 7,
        id: 105,
        name: "adsfsdaf",
        organisation: 1,
        orgName: "asdf",
      },
    ]);
    done();
  });

  test("Add User Item", (done) => {
    dispatch({
      type: actionTypes.ADD_USER_ITEM,
      payload: {
        slno: admin_initial_view_state.userData.length + 1,
        ...{
          id: 101,
          name: "sdaffdsa",
          email: "sadffdsfdasfdas@gmail.com",
          enabled: true,
          organisation: 2,
          orgName: "sdafsdaf",
          roles: [
            {
              id: 104,
              role: "ROLE_USER",
            },
          ],
          profiles: [],
        },
      },
    });
    expect(store.getState().admin.userData).toEqual([
      {
        id: 4,
        name: "downloadManager",
        email: "mailto:download@helicalinsight.com",
        enabled: true,
        organisation: "",
        orgName: "Null",
        roles: [
          {
            id: 2,
            role: "ROLE_USER",
          },
          {
            id: 4,
            role: "ROLE_DOWNLOAD",
          },
        ],
        profiles: [
          {
            id: 1,
            name: "asdfsadf",
            value: "sadfsdaf",
          },
        ],
        slno: "1",
      },

      {
        id: 2,
        name: "hiuser",
        email: "mailto:user@helicalinsight.com",
        enabled: true,
        organisation: "",
        orgName: "Null",
        roles: [
          {
            id: 2,
            role: "ROLE_USER",
          },
        ],
        profiles: [],
        slno: "3",
      },
      {
        id: 3,
        name: "hiviewer",
        email: "mailto:viewer@helicalinsight.com",
        enabled: true,
        organisation: "",
        orgName: "Null",
        roles: [
          {
            id: 3,
            role: "ROLE_VIEWER",
          },
        ],
        profiles: [],
        slno: "4",
      },
      {
        id: 101,
        slno: 5,
        name: "sdaffdsa",
        email: "sadffdsfdasfdas@gmail.com",
        enabled: true,
        organisation: 2,
        orgName: "sdafsdaf",
        roles: [
          {
            id: 104,
            role: "ROLE_USER",
          },
        ],
        profiles: [],
      },
    ]);
    done();
  });
});
