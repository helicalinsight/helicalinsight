import { updateDSToRenderWithTables } from "../../../../components/hi-metadata/utils";
import {argData} from "./6160.mock.data";

describe('To Test: Metadata sidebar table selection in Edit mode', () => {
    test('Tables fetched from Backend are already selected in sidebar', () =>{
        const [datasourceListToRender] = updateDSToRenderWithTables(argData)
        const derbyTables = datasourceListToRender[0].children[2].children[3].children;

        expect(derbyTables.filter(table => table.selected).length).toBe(4);
        expect(derbyTables[4].selected).toBe(false);
    })
})