import {useState, useEffect} from "react";
import tokenService from "../services/token.service";
import useIntervalFetchState from "../util/useIntervalFetchState";
import getErrorModal from "../util/getErrorModal";
import modalForm from "../util/modalForm";
import {Button, ButtonGroup, Table} from "reactstrap";
import {Link} from "react-router-dom";
import useFetchData from "../util/useFetchData";
import '../static/css/gameList.css'

const jwt = tokenService.getLocalAccessToken();

export default function GameList() {

    const authority = tokenService.getUser().roles[0];
    const id = tokenService.getUser().id;
    const user = useFetchData(`/api/v1/users/${id}`, jwt);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [messageCodigo, setMessageCodigo] = useState(null);
    const [visibleCodigo, setVisibleCodigo] = useState(false);
    const [partida, setPartida] = useState('');
    const [estaEnPartida, setEstaEnPartida] = useState(false);
    const [partidaUsuario, setPartidaUsuario] = useState(null);
    const [usuarioEstaEnPartidaId, setUsuarioEstaEnPartidaId] = useState(null);
    const [usuariosPartida, setUsuariosPartida] = useState(null);
    const [codigo, setCodigo] = useState('');
    const [partidaAcceder, setPartidaAcceder] = useState(null);
    const modal = getErrorModal(setVisible, visible, message);
    const modalCode = modalForm(setVisibleCodigo, visibleCodigo, messageCodigo, setCodigo, codigo);
    const [games, setGames] = useIntervalFetchState([], `/api/v1/game`, jwt, setMessage, setVisible, null, 1000);
    //Controller para los usuarios de una partida, para temporizador 

    useEffect(() => {
        for (let i = 0; i < games.length; i++) {
            for (let e = 0; e < games[i].users.length; e++) {
                if (games[i].users[e].id === id) {
                    if (!games[i].finish) {
                        setPartida(games[i].name);
                        setEstaEnPartida(true);
                        setPartidaUsuario(games[i]);
                        setUsuarioEstaEnPartidaId(e);
                        setUsuariosPartida(games[i].users)
                    }
                }
            }
        }
    }, [games, id]);

    useEffect(() => {
        if (partidaAcceder === codigo) {
            const updateGame = games.find((game) => game.code === partidaAcceder);
            updateGame.users.push(user);
            fetch("/api/v1/game" + (updateGame.id ? "/" + updateGame.id : ""), {
                method: "PUT", headers: {
                    Authorization: `Bearer ${jwt}`, Accept: "application/json", "Content-Type": "application/json",
                }, body: JSON.stringify(updateGame),
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
    }, [codigo]);

    function añadirJugador(gameId) {
        const updateGame = games.find((game) => game.id === gameId);
        updateGame.users.push(user);
        fetch("/api/v1/game" + (gameId ? "/" + gameId : ""), {
            method: "PUT", headers: {
                Authorization: `Bearer ${jwt}`, Accept: "application/json", "Content-Type": "application/json",
            }, body: JSON.stringify(updateGame),
        })
            .then((response) => response.json())
            .then((json) => {
                if (json.message) {
                    setMessage(json.message);
                    setVisible(true);
                }
                window.location.href = `lobby/${gameId}`;
            })
            .catch((message) => alert(message));
    }
    function eliminarInvitación(gameId,user){
        const updateGame = games.find((game) => game.id === gameId);
        const indexToRemove = updateGame.invitations.findIndex((friend) => friend.username === user.username);
        updateGame.invitations.splice(indexToRemove, 1);
        fetch("/api/v1/game" + (gameId ? "/" + gameId : ""), {
            method: "PUT", headers: {
                Authorization: `Bearer ${jwt}`, Accept: "application/json", "Content-Type": "application/json",
            }, body: JSON.stringify(updateGame),
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
    function eliminarJugador(gameId) {
        const updateGame = games.find((game) => game.id === gameId);
        updateGame.users.splice(usuarioEstaEnPartidaId, 1);
        fetch("/api/v1/game" + (gameId ? "/" + gameId : ""), {
            method: "PUT", headers: {
                Authorization: `Bearer ${jwt}`, Accept: "application/json", "Content-Type": "application/json",
            }, body: JSON.stringify(updateGame),
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

    const gamesList = games.map((game) => {
        if (authority === "ADMIN") {
            return (<tr key={game.id} style={{borderColor: "transparent"}}>
                <td className="table-game-list text-center">{game.name}</td>
                <td className="table-game-list text-center ">{game.code ? 'PRIVATE' : 'PUBLIC'}</td>
                <td className="table-game-list text-center ">{game.create}</td>
                <td className="table-game-list text-center">{game.start ? game.start : 'WAITING'}</td>
                <td className="table-game-list text-center">{game.finish ? game.finish : 'NOT FINISHED  '}</td>
                <td className="table-game-list text-center">{game.users.length + '/4'}</td>
                <td className="table-game-list text-center">
                    <ButtonGroup style={{display:"flex"}}>
                        {!game.start && <Button
                            className={"game-edit-button"}
                            onClick={() => {
                                window.location.href = "/EditGames/" + game.id
                            }}
                        >
                            Edit
                        </Button>}
                        {!game.start && <Button
                            className={"game-exit-button"}
                            onClick={() => fetch("/api/v1/game/" + game.id, {
                                method: "DELETE", headers: {
                                    Authorization: `Bearer ${jwt}`,
                                    Accept: "application/json",
                                    "Content-Type": "application/json",
                                }, body: JSON.stringify(game),
                            })
                                .then((json) => {
                                    if (json.message) {
                                        setMessage(json.message);
                                        setVisible(true);
                                    }
                                    window.location.href = "/games";
                                })
                                .catch((message) => alert(message))}
                        >
                            Delete
                        </Button>}
                        {game.start && !game.finish && <Button
                            className={"table-normal-button"}
                            tag={Link}
                            to={"/field/" + game.id + "/expecte"}
                        >
                            Expecte
                        </Button>}
                    </ButtonGroup>
                </td>
            </tr>);
        } else {
            if(!game.finish) {
                const isInvited = game.invitations.some(invitedFriend => invitedFriend.username === user.username);
                return (<tr key={game.id} style={{borderColor: "transparent"}}>
                    <td className="table-game-list text-center">{game.name}</td>
                    <td className="table-game-list text-center">{game.code ? 'PRIVATE' : 'PUBLIC'}</td>
                    <td className="table-game-list text-center">{game.start ? 'IN GAME' : 'WAITING'}</td>
                    <td className="table-game-list text-center">{game.users.length + '/4'}</td>
                    <td className="table-game-list text-center">
                        {(!game.start && game.users.length !== 4 && !estaEnPartida && !isInvited) && <Button
                            className={"table-normal-button"}
                            onClick={() => {
                                if (game.code !== '') {
                                    setMessageCodigo("Tiene codigo");
                                    setVisibleCodigo(true);
                                    setPartidaAcceder(game.code);
                                } else {
                                    añadirJugador(game.id);
                                }
                            }}
                        >
                            Join
                        </Button>}
                        {(!game.start && game.users.length !== 4 && !estaEnPartida && isInvited) && <Button
                            className={"table-normal-button"}
                            onClick={() => {
                                    añadirJugador(game.id);
                                    eliminarInvitación(game.id,user)
                            }}
                        >
                            Join Friend
                        </Button>}
                        {game.start && !game.finish && !estaEnPartida && <Button
                            className={"table-normal-button"}
                            tag={Link}
                            to={"/field/" + game.id + "/expecte" }
                        >
                            Expecte
                        </Button>}
                        {!game.start && estaEnPartida && game.id === partidaUsuario.id && (<ButtonGroup style={{maxWidth:"100% !important"}}>
                            <Button
                                className={"game-exit-button"}
                                onClick={() => {
                                    eliminarJugador(game.id);
                                }}>
                                Exit
                            </Button>
                            <Button
                                className={"table-normal-button"}
                                tag={Link}
                                to={`/lobby/${game.id}`}
                            >
                                Lobby
                            </Button>
                        </ButtonGroup>)}
                        {game.start && estaEnPartida && game.id === partidaUsuario.id && (<ButtonGroup>
                            <Button
                                className={"table-normal-button"}
                                tag={Link}
                                to={`/field/${game.id}`}
                            >
                                Rejoin
                            </Button>
                        </ButtonGroup>)}
                    </td>
                </tr>);
            }
        }
    });

    return (<div className="game-list-background">
        <div className="position-items">
            <text className="game-list-text">Game List</text>
            {modal}
            {modalCode}
            <div>
                <Table aria-label="games" className={"table-game"}>
                    <thead>
                    <tr>
                        <th width="10%" className="table-header-game-list text-center">Name</th>
                        <th width="5%" className="table-header-game-list text-center">Type</th>
                        {authority === "ADMIN" &&
                            <th width="15%" className="table-header-game-list text-center">Create</th>}
                        <th width="10%" className="table-header-game-list text-center">Start</th>
                        {authority === "ADMIN" &&
                            <th width="15%" className="table-header-game-list text-center">Finish</th>}
                        <th width="5%" className="table-header-game-list text-center">Players</th>
                        <th width="10%" className="table-header-game-list text-center">Actions</th>
                    </tr>
                    </thead>
                    <tbody>{gamesList}</tbody>
                </Table>
            </div>
            {authority !== "ADMIN" && estaEnPartida && <h5 className="status-text">You are currently in the game: {partida}</h5>}
            {authority !== "ADMIN" && !estaEnPartida &&
                <h5 className="status-text">You are not currently in any games.</h5>}
            {authority !== "ADMIN" && !estaEnPartida && <Button className="create-button" onClick={() => {
                window.location.href = "/CreateGames"
            }}>
                Create Game
            </Button>}
        </div>
    </div>);


}
