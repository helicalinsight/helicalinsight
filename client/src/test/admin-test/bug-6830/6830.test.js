import { getRolesEditTableData } from "../../../components/hi-admin/components/hi-userManagement/helperMethods";

    const activeRecord = {
        "id": 4,
        "name": "downloadManager",
        "email": "download@helicalinsight.com",
        "enabled": true,
        "organisation": "",
        "orgName": "Null",
        "roles": [
            {
                "id": 2,
                "role": "ROLE_USER"
            },
            {
                "id": 4,
                "role": "ROLE_DOWNLOAD"
            },
            {
                "id": 117,
                "role": "ROLE_ADMIN"
            }
        ],
        "profiles": [],
        "slno": "2"
    };
    const roleData = [
        {
            "id": 118,
            "name": "ROLE_USER",
            "organisation": 9,
            "orgName": "dvs",
            "slno": "1"
        },
        {
            "id": 117,
            "name": "ROLE_ADMIN",
            "organisation": 9,
            "orgName": "dvs",
            "slno": "2"
        },
        {
            "id": 122,
            "name": "ROLE_USER",
            "organisation": 11,
            "orgName": "csdc",
            "slno": "3"
        },
        {
            "id": 121,
            "name": "ROLE_ADMIN",
            "organisation": 11,
            "orgName": "csdc",
            "slno": "4"
        },
        {
            "id": 502,
            "name": "ROLE_USER",
            "organisation": 301,
            "orgName": "test",
            "slno": "5"
        },
        {
            "id": 501,
            "name": "ROLE_ADMIN",
            "organisation": 301,
            "orgName": "test",
            "slno": "6"
        },
        {
            "id": 504,
            "name": "ROLE_USER",
            "organisation": 302,
            "orgName": "dummy",
            "slno": "7"
        },
        {
            "id": 503,
            "name": "ROLE_ADMIN",
            "organisation": 302,
            "orgName": "dummy",
            "slno": "8"
        },
        {
            "id": 506,
            "name": "ROLE_USER",
            "organisation": 303,
            "orgName": "TEst",
            "slno": "9"
        },
        {
            "id": 505,
            "name": "ROLE_ADMIN",
            "organisation": 303,
            "orgName": "TEst",
            "slno": "10"
        },
        {
            "id": 601,
            "name": "ddd",
            "organisation": "",
            "orgName": "Null",
            "slno": "11"
        },
        {
            "id": 201,
            "name": "test1",
            "organisation": "",
            "orgName": "Null",
            "slno": "12"
        },
        {
            "id": 4,
            "name": "ROLE_DOWNLOAD",
            "organisation": "",
            "orgName": "Null",
            "slno": "13"
        },
        {
            "id": 3,
            "name": "ROLE_VIEWER",
            "organisation": "",
            "orgName": "Null",
            "slno": "14"
        },
        {
            "id": 2,
            "name": "ROLE_USER",
            "organisation": "",
            "orgName": "Null",
            "slno": "15"
        },
        {
            "id": 1,
            "name": "ROLE_ADMIN",
            "organisation": "",
            "orgName": "Null",
            "slno": "16"
        }
    ];
    const result= [
        {
            "id": 118,
            "name": "ROLE_USER | dvs",
            "organisation": 9,
            "orgName": "dvs",
            "slno": "1"
        },
        {
            "id": 117,
            "name": "ROLE_ADMIN | dvs",
            "organisation": 9,
            "orgName": "dvs",
            "slno": "2"
        },
        {
            "id": 122,
            "name": "ROLE_USER | csdc",
            "organisation": 11,
            "orgName": "csdc",
            "slno": "3"
        },
        {
            "id": 121,
            "name": "ROLE_ADMIN | csdc",
            "organisation": 11,
            "orgName": "csdc",
            "slno": "4"
        },
        {
            "id": 502,
            "name": "ROLE_USER | test",
            "organisation": 301,
            "orgName": "test",
            "slno": "5"
        },
        {
            "id": 501,
            "name": "ROLE_ADMIN | test",
            "organisation": 301,
            "orgName": "test",
            "slno": "6"
        },
        {
            "id": 504,
            "name": "ROLE_USER | dummy",
            "organisation": 302,
            "orgName": "dummy",
            "slno": "7"
        },
        {
            "id": 503,
            "name": "ROLE_ADMIN | dummy",
            "organisation": 302,
            "orgName": "dummy",
            "slno": "8"
        },
        {
            "id": 506,
            "name": "ROLE_USER | TEst",
            "organisation": 303,
            "orgName": "TEst",
            "slno": "9"
        },
        {
            "id": 505,
            "name": "ROLE_ADMIN | TEst",
            "organisation": 303,
            "orgName": "TEst",
            "slno": "10"
        },
        {
            "id": 601,
            "name": "ddd | Null",
            "organisation": "",
            "orgName": "Null",
            "slno": "11"
        },
        {
            "id": 201,
            "name": "test1 | Null",
            "organisation": "",
            "orgName": "Null",
            "slno": "12"
        },
        {
            "id": 4,
            "name": "ROLE_DOWNLOAD | Null",
            "organisation": "",
            "orgName": "Null",
            "slno": "13"
        },
        {
            "id": 3,
            "name": "ROLE_VIEWER | Null",
            "organisation": "",
            "orgName": "Null",
            "slno": "14"
        },
        {
            "id": 2,
            "name": "ROLE_USER | Null",
            "organisation": "",
            "orgName": "Null",
            "slno": "15"
        },
        {
            "id": 1,
            "name": "ROLE_ADMIN | Null",
            "organisation": "",
            "orgName": "Null",
            "slno": "16"
        }
    ];

describe("edit roles test", () => {
    test("testing getRolesEditTableData func", (done) => {
        expect(getRolesEditTableData({activeRecord , roleData})).toEqual(result);
        done();
    });
});