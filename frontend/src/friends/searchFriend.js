import React, { useState } from "react";
import { Button, Form, Input, Label, Table } from "reactstrap";
import '../App.css';
import tokenService from '../services/token.service';
import getErrorModal from "../util/getErrorModal";
import useFetchData from "../util/useFetchData";


export default function SearchFriends() {
    const jwt = tokenService.getLocalAccessToken();
    const username = tokenService.getUser().username;
    const id = tokenService.getUser().userId;
    const friends = useFetchData(`/api/v1/users/friends/${username}`, jwt);
    const users = useFetchData('/api/v1/users', jwt);
    const [inputValue, setInputValue] = useState('');
    const [message, setMessage] = useState(null)
    const [visible, setVisible] = useState(false)
    const [friendToSearch, setFriendToSearch] = useState()
    const modal = getErrorModal(setVisible, visible, message);


    function actualizarAmigo(friendToUpdate) {
        fetch(`/api/v1/users/friends/${username}`, {
            method: "POST",
            headers: {
                Authorization: `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json"
            },
            body: JSON.stringify(friendToUpdate),
        }).then(response => response.json())
            .then(data => {
                console.log("Amigo añadido exitosamente", data);
                window.location.href = "/MyFriends";
            })
            .catch(error => {
                console.error("Error al añadido amigo:", error);
            });
    }

    const friendsList = friendToSearch ?
        <tr key={friendToSearch.id}>
            <td className="text-center">{friendToSearch.username}</td>
            <td className="text-center">{friendToSearch.firstName}</td>
            <td className="text-center">{friendToSearch.lastName}</td>
            <td className="text-center">{friendToSearch.age}</td>
            <td className="text-center">
                <Button
                    size="sm"
                    color="primary"
                    aria-label={"Añadir-" + friendToSearch.username}
                    onClick={() => { actualizarAmigo(friendToSearch); window.location.href = "/SearchFriends" }}
                >
                    Añadir
                </Button>
            </td>
        </tr>
        : null;

    function searchFriend(inputValue) {
        setFriendToSearch(inputValue);
        if (friends.some(friend => friend.username === inputValue)) {
            setMessage("El usuario que quiere añadir ya es su amigo");
            setVisible(true);
            return;
        }

        if (inputValue === username) {
            setMessage("¡No te puedes añadir a ti mismo como amigo!");
            setVisible(true);
            return;
        }
        const foundUser = users.find(user => user.username === inputValue);

        if (foundUser) {
            setFriendToSearch(foundUser);
        } else {
            setMessage("¡Ese usuario no existe!");
            setVisible(true);
        }
    }


    return (
        <div>
            {modal}
            <div className="custom-form-input" style={{ maxWidth: '400px', marginTop: '120px', marginLeft: '720px' }}>
                <Form style={{ backgroundColor: 'transparent' }}>
                    <Label for="friend" className="custom-form-input-label" style={{ color: 'black' }}>
                        Nombre de usuario de tu amigo
                    </Label>
                    <Input
                        type="text"
                        name="friend"
                        id="friend"
                        onChange={(e) => setInputValue(e.target.value)}
                        className="custom-input" />
                </Form>
                <Button
                    onClick={() => searchFriend(inputValue)}
                    style={{ backgroundColor: '#976DD0', marginTop: '10px', marginLeft: '150px' }}>Buscar</Button>
            </div>
            <div className="prueba-pruebisima" style={{ marginTop: 50 }}>
                <Table aria-label="users" className="mt-4" bordered hover style={{ maxWidth: '1000px', background: '#f2f2f2', fontSize: '17px' }}>
                    <thead>
                        <tr>
                            <th width="5%">Username</th>
                            <th width="5%">Firstname</th>
                            <th width="5%">Lastname</th>
                            <th width="5%">Age</th>
                            <th width="5%">Accion</th>
                        </tr>
                    </thead>
                    <tbody>
                        {friendsList}
                    </tbody>
                </Table>
            </div>

        </div>
    );
}