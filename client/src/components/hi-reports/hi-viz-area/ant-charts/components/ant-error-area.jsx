const ErrorArea = ({ message }) => {
    return (
      <div style={{ height: "100%", backgroundColor: "#f1f1f1", fontWeight: 800, padding: "0 5px" }}>
        {message}
      </div>
    );
  };

export default ErrorArea