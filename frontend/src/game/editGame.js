import React, { useState } from 'react';
import { Alert } from "reactstrap";
import FormGenerator from "../components/formGenerator/formGenerator";
import tokenService from "../services/token.service";
import useFetchData from "../util/useFetchData";
import "../static/css/admin/adminPage.css";
import {createGameFormInputs} from "./form/createGameFormInputs"
import getIdFromUrl from "../util/getIdFromUrl";

const jwt = tokenService.getLocalAccessToken();

export default function EditGame(){
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const loginFormRef = React.createRef(); 
  const id = getIdFromUrl(2);
  const game = useFetchData(`/api/v1/game/${id}`,jwt);

  async function handleSubmit({ values }) {
    game.name = values.name;
    game.code = values.code;
    setMessage(null);
    await fetch("/api/v1/game/" + id, {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${jwt}`,
          Accept: "application/json",
          "Content-Type": "application/json",
        },
        body: JSON.stringify(game),
      })
      .then((response) => response.json())
      .then((json) => {
        if (json.message) {
          setMessage(json.message);
          setVisible(true);
        }
        window.location.href = "/games";
      })
      .catch((message) => alert(message));           
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
            buttonText="Update"
            buttonClassName="auth-button"
          />
        </div>
      </div>
        </div>
    );  
}
