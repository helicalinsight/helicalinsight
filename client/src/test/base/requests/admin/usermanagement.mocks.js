const orgData = {
  data: {
    organisations: [
      { slno: "1", id: 2, name: "dsafadsf", description: "dsafsdfa" },
      { slno: "2", id: 3, name: "adsffdas", description: "adsffasd" },
      { slno: "3", id: 4, name: "dasfdfsadfsa", description: "dsafdfsafdsa" },
    ],
    total: 3,
  },
};

const userData = {
  data: {
    users: [
      {
        slno: "1",
        id: 4,
        name: "downloadManager",
        email: "download@helicalinsight.com",
        enabled: false,
        organisation: "",
        orgName: "Null",
        roles: [
          { id: 1, role: "ROLE_ADMIN" },
          { id: 4, role: "ROLE_DOWNLOAD" },
          { id: 2, role: "ROLE_USER" },
          { id: 3, role: "ROLE_VIEWER" },
        ],
        profiles: [
          {
            id: 3,
            name: "dasdsafddsafdsaffdssfasdfdsaf",
            value: "adsfasdfdsfdsf",
          },
        ],
      },
      {
        slno: "2",
        id: 1,
        name: "hiadmin",
        email: "admin@helicalinsight.com",
        enabled: true,
        organisation: "",
        orgName: "Null",
        roles: [{ id: 1, role: "ROLE_ADMIN" }],
        profiles: [
          { id: 4, name: "dasdsafdsfasdfdsafdsfdsf", value: "adsfasdfdsfdsf" },
        ],
      },
      {
        slno: "3",
        id: 2,
        name: "hiuser",
        email: "user@helicalinsight.com",
        enabled: true,
        organisation: "",
        orgName: "Null",
        roles: [
          { id: 1, role: "ROLE_ADMIN" },
          { id: 2, role: "ROLE_USER" },
          { id: 3, role: "ROLE_VIEWER" },
        ],
        profiles: [{ id: 6, name: "dasfdsaffds", value: "sdafdsdsf" }],
      },
      {
        slno: "4",
        id: 3,
        name: "hiviewer",
        email: "viewer@helicalinsight.com",
        enabled: true,
        organisation: "",
        orgName: "Null",
        roles: [{ id: 3, role: "ROLE_VIEWER" }],
        profiles: [
          { id: 5, name: "dsfsdafsdfdssfd", value: "dsfdsffsdfddfsd" },
        ],
      },
    ],
    total: 4,
  },
};

const roleData = {
  data: {
    total: 10,
    roles: [
      {
        slno: "1",
        id: 1,
        name: "ROLE_ADMIN",
        organisation: "",
        orgName: "Null",
      },
      {
        slno: "2",
        id: 2,
        name: "ROLE_USER",
        organisation: "",
        orgName: "Null",
      },
      {
        slno: "3",
        id: 3,
        name: "ROLE_VIEWER",
        organisation: "",
        orgName: "Null",
      },
      {
        slno: "4",
        id: 4,
        name: "ROLE_DOWNLOAD",
        organisation: "",
        orgName: "Null",
      },
      {
        slno: "5",
        id: 7,
        name: "ROLE_ADMIN",
        organisation: 2,
        orgName: "dsafadsf",
      },
      {
        slno: "6",
        id: 8,
        name: "ROLE_USER",
        organisation: 2,
        orgName: "dsafadsf",
      },
      {
        slno: "7",
        id: 9,
        name: "ROLE_ADMIN",
        organisation: 3,
        orgName: "adsffdas",
      },
      {
        slno: "8",
        id: 10,
        name: "ROLE_USER",
        organisation: 3,
        orgName: "adsffdas",
      },
      {
        slno: "9",
        id: 11,
        name: "ROLE_ADMIN",
        organisation: 4,
        orgName: "dasfdfsadfsa",
      },
      {
        slno: "10",
        id: 12,
        name: "ROLE_USER",
        organisation: 4,
        orgName: "dasfdfsadfsa",
      },
    ],
  },
};

const orgItemDelete = {
  data: {
    status: 1,
    response: { message: "Organization deleted successfully " },
  },
};

const roleItemDelete = {
  data: { status: 1, response: { message: "Role deleted successfully " } },
};

const userItemDelete = {
  data: { status: 1, response: { message: "User deleted successfully" } },
};

const userItemAdd = {
  data: {
    status: 1,
    response: { message: "User created successfully.", id: 5 },
  },
};

const orgItemAdd = {
  data: {
    status: 1,
    response: { message: "Organization added successfully", id: 5 },
  },
};

const roleItemAdd = {
  data: {
    status: 1,
    response: {
      message: "Role added successfully",
      id: "15",
      orgName: "adsffdas",
    },
  },
};

const profileItemAdd = {
  data: {
    status: 1,
    response: { message: "Profile added successfully.", id: 8 },
  },
};

const profileItemDelete = {
  data: { status: 1, response: { message: "Profile successfully deleted" } },
};

const userItemEdit = {
  data: { status: 1, response: { message: "User updated successfully. " } },
};

const profileItemEdit = {
  data: { status: 1, response: { message: "Profile updated successfully. " } },
};

const roleItemEdit = {
  data: { status: 1, response: { message: "Role updated successfully" } },
};

const errorMock = {
  data: {
    status: 0,
    response: {
      message:
        "Error: FormValidationException: The formData does not have logLevel information",
    },
  },
};

const usermanagementMocks = {
  roleItemAdd,
  orgItemAdd,
  orgData,
  errorMock,
  userData,
  roleData,
  orgItemDelete,
  profileItemAdd,
  roleItemDelete,
  userItemDelete,
  userItemAdd,
  profileItemDelete,
  userItemEdit,
  profileItemEdit,
  roleItemEdit,
};

export default usermanagementMocks;
