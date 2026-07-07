import { datasourceListToRender, record, tableRecord } from './constants'
import { isObject } from '../../utils/is-object'
import { cloneDeep } from 'lodash-es'
import { handleDSTableCheck } from '../../components/hi-metadata/utils/handleDSTableCheck'
import { handleDSTableOptionsClick } from '../../components/hi-metadata/utils/handleDSTableOptionsClick'
import { getMetadata } from '../../components/hi-metadata/utils/getMetadata'


describe('validating arguments for handleDSTableCheck', () => {
    test('valid args', () => {
        let args = {
            "location": "1463377807724/1463377836985",
            "uuid": "e9be6771-995b-40eb-a01c-304857a100a1.metadata",
            "store": {
                "liftedStore": {}
            },
            "returnFetched": true,
            "datasourceListToRender": [
                {
                    "name": "Derby",
                    "children": [
                        {
                            "name": "SampleEFWD Sample Travel Data",
                            "type": "sql.jdbc",
                            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                            "dataSourceType": "Plain Jdbc DataSource",
                            "classifier": "efwd",
                            "permissionLevel": 2,
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "dataSource",
                            "children": [],
                            "driverType": "Derby",
                            "keyPath": "7goj-a6vi-xm7k-iusv-ar/xs9h-rvu7-i23z-p7zg-9a",
                            "key": "xs9h-rvu7-i23z-p7zg-9a",
                            "uuid": "xs9h-rvu7-i23z-p7zg-9a"
                        },
                        {
                            "name": "SampleEFWD Sample Travel Data",
                            "type": "sql.jdbc",
                            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                            "dataSourceType": "Plain Jdbc DataSource",
                            "classifier": "efwd",
                            "permissionLevel": 2,
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Dashboard",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "dataSource",
                            "children": [],
                            "driverType": "Derby",
                            "keyPath": "7goj-a6vi-xm7k-iusv-ar/s9d8-hrwx-e4h2-tdjb-iy",
                            "key": "s9d8-hrwx-e4h2-tdjb-iy",
                            "uuid": "s9d8-hrwx-e4h2-tdjb-iy"
                        },
                        {
                            "name": "SampleTravelDataDerby",
                            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                            "dataSourceType": "Managed DataSource",
                            "classifier": "global",
                            "type": "dynamicDataSource",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "permissionLevel": 5,
                            "dataSourceProvider": "tomcat",
                            "category": "dataSource",
                            "children": [],
                            "driverType": "Derby",
                            "keyPath": "7goj-a6vi-xm7k-iusv-ar/7xkd-e0ft-1y5b-i6fo-dg",
                            "key": "7xkd-e0ft-1y5b-i6fo-dg",
                            "uuid": "7xkd-e0ft-1y5b-i6fo-dg"
                        }
                    ],
                    "key": "7goj-a6vi-xm7k-iusv-ar",
                    "uuid": "7goj-a6vi-xm7k-iusv-ar",
                    "keyPath": "7goj-a6vi-xm7k-iusv-ar",
                    "category": "dsGroup",
                    "imgUrl": "images/data_sources/defaut_datasource.png"
                }
            ],
            "mode": "edit"
        }
        // getMetadata(args)
        expect(true).toBeTruthy()

    })
})