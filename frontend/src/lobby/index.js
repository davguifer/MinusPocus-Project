import React, {useEffect, useState} from "react";
import {Button, Table} from "reactstrap";
import {Link} from "react-router-dom";
import useFetchState from "../util/useFetchState";
import useIntervalFetchState from "../util/useIntervalFetchState";
import tokenService from "../services/token.service";
import useFetchData from "../util/useFetchData";
import GameList from "../game";
import {FaCrown} from "react-icons/fa";
import '../static/css/lobby.css'
import FriendList from "../friends/friendList";

const jwt = tokenService.getLocalAccessToken();

export default function Lobby() {
    const currentUserId = tokenService.getUser().id;
    const pathSegments = window.location.pathname.split("/");
    const gameId = pathSegments[pathSegments.length - 1];
    const user = useFetchData("/api/v1/users/" + currentUserId, jwt);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [delay, setDelay] = useState(1000);
    const [game, setGames] = useIntervalFetchState([], `/api/v1/game/${gameId}`, jwt, setMessage, setVisible, null, 100);
    const [users, setUsers] = useState([]);
    const [tam, setTam] = useState(users.length);
    const wizardColors = ['red', 'yellow', 'green', 'blue']
    const [showColor, setShowColor] = useState(false)

    useEffect(() => {
        // Check if the game has been loaded
        if (game && game.users) {
            setUsers(game.users);
            if (tam != users) {
                setTam(users.length);
            }

        }
        if (game.start) {
            window.location.href = '/field/' + gameId;
        }
    }, [game]);

    function eliminarJugador(gameId) {
        const updatedGame = {...game};
        const userIndex = updatedGame.users.findIndex(user => user.id === currentUserId);

        if (userIndex !== -1) {
            updatedGame.users.splice(userIndex, 1);
        }
        fetch("/api/v1/game" + (gameId ? "/" + gameId : ""), {
            method: "PUT",
            headers: {
                Authorization: `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: JSON.stringify(updatedGame),
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

    function startGame() {
        fetch("/api/v1/game/" + gameId + "/start", {
            method: "PUT",
            headers: {
                Authorization: `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
        })
            .then(function (response) {
                if (response.status === 201) {
                    return response.json();
                }
            })
            .catch((message) => alert(message));
    }

    const listUsers = users.map((user, index) => {
        const avatar = user ? user.avatar : 'https://www.cotopaxi.com.ec/sites/default/files/2020-08/BLANCO%20760X440PX_0.png';
        const userWidth = 100 / users.length;

        return (
            <td key={index} style={{
                backgroundColor: "transparent",
                maxWidth: `${userWidth}%`
            }}>
                <div style={{backgroundColor: 'transparent'}}>
                    <div style={{textAlign: 'center', backgroundColor: "transparent"}}>
                        {index === 0 && <FaCrown style={{
                            fontSize: '1vw',
                            marginRight: '2%',
                            display: 'compact',
                            textAlign: 'center'
                        }}/>}
                        <text style={{
                            fontFamily: "Immortal",
                            fontWeight: "bolder",
                            fontSize: '1vw',
                            display: 'compact',
                            marginBottom: '0.5vw'
                        }}>Player {index + 1}</text>
                    </div>
                    <img className="lobby-avatar" src={avatar}/>
                    <p style={{
                        fontSize: '1vw',
                        fontFamily: 'Immortal',
                        fontWeight: 'bolder',
                        backgroundColor: "transparent",
                        textAlign: 'center',
                        marginTop: '0.5vw'
                    }}>
                        {user.username}
                    </p>
                </div>
            </td>
        );
    });

    return (
        <div className="lobby-background">
            <div className="position-items">
                <h1 className="lobby-Game-text">{game.name}</h1>
                <Table className="lobby-player-list">
                    <tbody>
                    {listUsers}
                    </tbody>
                </Table>
                <div className="text-center" style={{marginTop: '2vw'}}>
                    <div className="lobby-button-action">
                        <Link to="/games">
                            <Button className="lobby-action-button" color="primary">Back to Games</Button>
                        </Link>
                        <div className="text-center">
                            {((users.length > 1 && user) ? users[0].id === user.id : false) && <Button
                                className="start-button text-center"
                                onClick={() => {
                                    startGame();
                                }}>
                                START GAME
                            </Button>}
                        </div>
                        <div style={{display:"flex",flexDirection:"row"}}>
                            <Button
                                className={`lobby-exit-button`}
                                onClick={() => {
                                    eliminarJugador(gameId);
                                }}>
                                Leave
                            </Button>
                            {<FriendList numLobbyPlayer={users.length} gameId={gameId}/>}

                        </div>
                    </div>

                </div>
            </div>
        </div>
    );
}
