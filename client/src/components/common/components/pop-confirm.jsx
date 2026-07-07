
const HIPopConfirm = ({ title = undefined, description = undefined }) => {
    return (
        <div
            style={{
                maxWidth: "550px",
                marginBottom: '5px',
                width: "350px",
                wordWrap: "break-word"
            }}
        >
            <div>
                <h3><b>{title}</b></h3>
            </div>
            <p style={{ Font: "bold" }}>{description}</p>
        </div>
    );
};

export default HIPopConfirm;
