import {updateDatasourcesInMetadata} from '../../redux/reducers/metadata.reducer'


    let removedDataSources = [{
        id: "1",
        type: "dynamicDataSource",
        baseType: "global.jdbc",
        catSchemaPredicted: false,
        sync: false,
        catalog: "",
        schema: "HIUSER",
        connId: "uegeh",
        classifier: "db.workflow",
        datasourceName: "SampleTravelDataDerby",
        dsKeyPath: "fvre-qenq-yn2b-mcc8-nb/a3c2-k0fx-yupv-3n4z-uq/dbi6-ws7d-rtq8-pqq7-zm",
        driverType: "Derby",
        database: "HIUSER",
    }]

    let dataSource = []

    let tables = {
        "geo_cordinates": {
            "id": "be534112989b616b194bc59c2fb25a42",
            "name": "geo_cordinates",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "fvre-qenq-yn2b-mcc8-nb/a3c2-k0fx-yupv-3n4z-uq/dbi6-ws7d-rtq8-pqq7-zm/z1d7-bhnj-1bue-7bir-b4",
            "key": "z1d7-bhnj-1bue-7bir-b4",
            "alias": "geo_cordinates",
            "uuid": "z1d7-bhnj-1bue-7bir-b4",
            "connId": "uegeh",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "uegeh",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "fvre-qenq-yn2b-mcc8-nb/a3c2-k0fx-yupv-3n4z-uq/dbi6-ws7d-rtq8-pqq7-zm",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "geo_cordinates_uegeh",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "geo_cordinates"
        }
    }

    describe('testing 4723', () => {
        test('checking with removedDatasource and datasource as empty array - checking if result is array', () => {
            let result = updateDatasourcesInMetadata({ tables, removedDataSources, dataSource })
            console.log(result)
            expect(Array.isArray(result)).toBeTruthy()
        })

        test('checking with removedDatasource and datasource as empty array - checking if seconfd ragument is true', () => {
            let result = updateDatasourcesInMetadata({ tables, removedDataSources, dataSource })
            console.log(result)
            expect(result[1]).toBeTruthy()
        })

        test('checking with removedDatasource and datasource as empty array - cheking if first arg is array', () => {
            let result = updateDatasourcesInMetadata({ tables, removedDataSources, dataSource })
            console.log(result)
            expect(Array.isArray(result[0])).toBeTruthy()
        })

        test('checking with removedDatasource and datasource as valid array', () => {
            let result = updateDatasourcesInMetadata({ tables, removedDataSources, dataSource: removedDataSources })
            console.log(result)
            expect(Array.isArray(result)).toBeTruthy()
        })

        test('checking with removedDatasource and datasource as valid array', () => {
            let result = updateDatasourcesInMetadata({ tables, removedDataSources, dataSource: removedDataSources })
            console.log(result)
            expect(result[1]).not.toBeTruthy()
        })

        test('checking with removedDatasource and datasource as valid array', () => {
            let result = updateDatasourcesInMetadata({ tables, removedDataSources, dataSource: removedDataSources })
            console.log(result)
            expect(Array.isArray(result[0])).toBeTruthy()
        })
    })


