import React, { useState } from "react";
import { Button, Table } from "reactstrap";
import '../App.css';
import tokenService from '../services/token.service';
import getErrorModal from "../util/getErrorModal";
import useFetchData from "../util/useFetchData";


export default function Friend() {
    const jwt = tokenService.getLocalAccessToken();
    const username = tokenService.getUser().username;
    const id = tokenService.getUser().userId;
    const friends = useFetchData(`/api/v1/users/friends/${username}`, jwt);
    const users = useFetchData('/api/v1/users', jwt);
    const [inputValue, setInputValue] = useState('');
    const [message, setMessage] = useState(null)
    const [visible, setVisible] = useState(false)
    const [friendToSearch, setFriendToSearch] = useState(false)
    const modal = getErrorModal(setVisible, visible, message);

    const friendsList2 =
        friends.map((friendItem) => {
            return (
                <tr key={friendItem.id}>
                    <td className="text-center">{friendItem.username}</td>
                    <td className="text-center">{friendItem.firstName}</td>
                    <td className="text-center">{friendItem.lastName}</td>
                    <td className="text-center">{friendItem.age}</td>
                    <td className="text-center">
                        <Button
                            size="sm"
                            color="primary"
                            aria-label={"Eliminar-" + friendItem.username}
                            onClick={() => {eliminarAmigo(friendItem);window.location.href = "/MyFriends" }}
                        >
                            Eliminar amigo
                        </Button>
                    </td>
                </tr>

            );
        });

    function eliminarAmigo(friendToDelete) {
        fetch(`/api/v1/users/friends/${username}`, {
            method: "DELETE",
            headers: {
                Authorization: `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json"
            },
            body: JSON.stringify(friendToDelete),
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Error al eliminar amigo: ${response.status}`);
                }
                console.log("Amigo eliminado exitosamente");

            })
            .catch(error => {
                console.error("Error al eliminar amigo:", error);
            })
            .finally(() => {
                window.location.href = "/MyFriends";
            });
    }

    return (
        <div className="prueba-pruebisima">
            <Table aria-label="users" className="mt-4" bordered hover style={{ maxWidth: '1000px', background: '#f2f2f2', fontSize: '17px', marginTop: '500px' }}>
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
                {friendsList2}
                </tbody>
            </Table>
        </div>
    );

}
