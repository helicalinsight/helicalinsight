import { CubeEditor } from "./cubeEditor";
// import { CubeMetadata } from "./cubeMetadata";
import "./cube.scss";
import SaveActions from "../hi-metadata/components/editor/saveActions";

export function Cube({ showBusinessFields = true }) {
    return <div className="cube-content">
        {/* <CubeMetadata /> */}
        {true ? <CubeEditor showBusinessFields={showBusinessFields} /> : <SaveActions />}
    </div>
}