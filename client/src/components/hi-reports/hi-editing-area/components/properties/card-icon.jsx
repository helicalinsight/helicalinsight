// This component is not used in the entire application.
export const CardIcon = ({key, label}) => {
    if (key && label) {
        return (
            <div key={key}>
                <span>{key}</span>
                <span>{label}</span>
            </div>
          );
    }
    return null
};