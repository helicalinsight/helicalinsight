import { CubeEditor } from "./cubeEditor";
// import { CubeMetadata } from "./cubeMetadata";
import "./cube.scss";
import SaveActions from "../hi-metadata/components/editor/saveActions";

export function Cube() {
    return <div className="cube-content">
        {/* <CubeMetadata /> */}
        {true ? <CubeEditor /> : <SaveActions />}
    </div>
}