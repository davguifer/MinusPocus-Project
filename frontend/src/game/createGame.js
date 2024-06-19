import React, { useState } from 'react';
import { Alert } from "reactstrap";
import FormGenerator from "../components/formGenerator/formGenerator";
import tokenService from "../services/token.service";
import useFetchData from "../util/useFetchData";
import "../static/css/admin/adminPage.css";
import {createGameFormInputs} from "./form/createGameFormInputs"

const jwt = tokenService.getLocalAccessToken();

export default function CreateGame(){
  const [message, setMessage] = useState(null)
  const loginFormRef = React.createRef();  
  const id = tokenService.getUser().id;
  const user = useFetchData(`/api/v1/users/${id}`, jwt);

  async function handleSubmit({ values }) {
    values['create']  =  new Date(Date.now());
    values['round'] = 1;
    values['start'] = null;
    values['finish'] = null;
    values['complete'] = false;
    values['users'] = [user];
    values['spellsDeck'] = [];
    values['ingredientsDeck'] = [];
    const reqBody = values;
    setMessage(null);
    await fetch("/api/v1/game", {
      method: "POST",
      headers: { 
        Authorization: `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json" 
      },
      body: JSON.stringify(reqBody),
    }).then(function (response) {
        if (response.status === 201){
          
          
          return response.json();
        } 
        else if (response.status === 406)
          return Promise.reject("You are already in a game")
        else return Promise.reject("Wrong name or code");
      }).then(function (responseData){
        window.location.href = "/lobby/"+responseData.id;
      }).catch((error) => {         
        setMessage(error);
      });            
  }

  
    return (
        <div className={"auth-page-background"}>
      <div className="auth-page-container">
        {message ? (
          <Alert color="primary">{message}</Alert>
        ) : (
          <></>
        )}

        <h1>Game</h1>

        <div className="auth-form-container">
          <FormGenerator
            ref={loginFormRef}
            inputs={createGameFormInputs}
            onSubmit={handleSubmit}
            numberOfColumns={1}
            listenEnterKey
            buttonText="Create"
            buttonClassName="auth-button"
          />
        </div>
      </div>
        </div>
    );  
}
