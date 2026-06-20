
import './InputField.css'; 

const InputField = ({ label, type, name, value, onChange, placeholder, required = true }) => {
    return (
        <div className="input-field-container">
            <label className="input-field-label">{label}</label>
            <input
                type={type}
                name={name}
                value={value}
                onChange={onChange}
                placeholder={placeholder}
                required={required}
                className="input-field-element"
            />
        </div>
    );
};

export default InputField;