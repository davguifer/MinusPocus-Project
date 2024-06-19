import { formValidators } from "../../../validators/formValidators";


/* Esta funcion es para que al hacer click en avatar te deje selccionar una imagen de tipo JPG o PNG en tu explorador de archivos del ordenador,pero no se si funciona
const handleFileChange = (event) => {
  const fileInput = event.target;
  const selectedFile = fileInput.files[0];

  if (selectedFile) {
    // Asegúrate de que el archivo sea JPG o PNG
    if (selectedFile.type === "image/jpeg" || selectedFile.type === "image/png") {
      // Aquí puedes realizar cualquier acción necesaria con el archivo seleccionado
      // Por ejemplo, puedes cargar la imagen al servidor o mostrar una vista previa en tu interfaz de usuario.
    } else {
      // El usuario seleccionó un archivo que no es JPG o PNG, puedes mostrar un mensaje de error.
      alert("Por favor, selecciona una imagen JPG o PNG.");
    }
  }
};*/

export const registerFormPlayerInputs = [
  {
    tag:"firstName",
    name: "firstName",
    type: "text",
    defaultValue: "",
    isRequired: true,
    validators: [formValidators.notEmptyValidator], 
  },
  {
    tag: "lastName",
    name: "lastName",
    type: "text",
    defaultValue: "",
    isRequired: true,
    validators: [formValidators.notEmptyValidator],
  },
  {
    tag: "age",
    name: "age",
    type: "text", 
    defaultValue: "",
    isRequired: true,
    validators: [formValidators.notEmptyValidator],
  },
  {
    tag: "email",
    name: "email",
    type: "text",
    defaultValue: "",
    isRequired: true,
    validators: [formValidators.notEmptyValidator],
  },
  {
    tag: "username",
    name: "username",
    type: "text",
    defaultValue: "",
    isRequired: true,
    validators: [formValidators.notEmptyValidator],
  },
  {
    tag: "password",
    name: "password",
    type: "text",
    defaultValue: "",
    isRequired: true,
    validators: [formValidators.notEmptyValidator],
  },
  {
    tag: "avatar",
    name: "avatar",
    type: "text",
    defaultValue: "https://cdn-icons-png.flaticon.com/512/3607/3607444.png",
    isRequired: false,
    validators: [],
    //onChange: handleFileChange,  Esta función manejará la selección de archivos
  }
  
];
