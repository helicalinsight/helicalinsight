const data = {
  delete: {
    title: "Delete",
    description: "Are you sure you want to delete this item?",
  },
};

const PopconfirmBody = ({ intent, description = undefined, width }) => {
  return (
    <div
      style={{
        maxWidth: "550px",
        marginBottom: '5px',
        width: width || "350px",
        wordWrap: "break-word"
      }}
    >
      <div>
        <h3 ><b>{data[intent]?.title}</b></h3>
      </div>
      <p style={{ Font: "bold" }}>{description ?? data[intent]?.description}</p>
    </div>
  );
};

export default PopconfirmBody;
