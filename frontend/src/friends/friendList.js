// FriendList.js
import React, {useEffect, useState} from "react";
import '../static/css/friends/friendList.css'
import {Button} from "reactstrap";
import friendIco from '../friends/assets/friends-icon.png'
import tokenService from "../services/token.service";
import useFetchData from "../util/useFetchData";
import useIntervalFetchState from "../util/useIntervalFetchState";
import useFetchState from "../util/useFetchState";
import {response} from "msw";

const FriendList = ({numLobbyPlayer, gameId}) => {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [friendListVisible, setFriendListVisible] = useState(false);
    const username = tokenService.getUser().username;
    const jwt = tokenService.getLocalAccessToken();
    const friends = useFetchData(`/api/v1/users/friends/${username}`, jwt);
    const [game, setGame] = useFetchState([], `/api/v1/game/${gameId}`, jwt, setMessage, setVisible, null);
    const [invitedFriends, setInvitedFriends] = useState(game.invitations)

    useEffect(() => {
        setInvitedFriends(game.invitations || []);
    }, [game]);
    const handleToggle = () => {
        setFriendListVisible(!friendListVisible);
    };

    function invitePlayer(friend) {
        if (numLobbyPlayer < 4) {
            const isAlreadyInvited = invitedFriends.some(invitedFriend => invitedFriend.username === friend.username);
            if (!isAlreadyInvited) {
                const updatedInvitations = [...invitedFriends, friend];
                const updatedGame = {...game}
                updatedGame.invitations = updatedInvitations
                setInvitedFriends(updatedInvitations)
                setGame(updatedGame)

                fetch(`/api/v1/game/${gameId}`, {
                    method: 'PUT',
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(updatedGame),
                }).then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error ${response.status}`);
                    }
                    return response.json();
                })
                    .then(updatedData => {
                        console.log('Data updated:', updatedData);
                    })
                    .catch(error => {
                        console.error('Error updating data:', error);
                    });

                window.confirm(`${friend.username} se ha invitado`);
            } else {
                window.confirm(`${friend.username} ya ha sido invitado`);
            }
        } else {
            window.confirm('Número máximo de jugadores en la sala');
        }
    }

    function useShowInvited() {
        window.confirm(game.invitations.length)
    }

    return (

        <div className={"friend-popup-container"}>
            <div style={{padding: '1vw'}}
                 className={`friend-list ini-state ${friendListVisible ? "visible" : "hidden"}`}>
                <div className="friend-list-header">
                    <text className={'pop-up-header'}>Your Friends</text>
                    <Button className={'friend-close-button'} onClick={handleToggle}>X</Button>
                </div>
                {friends.length !== 0 && <table>
                    {friends.map((friend) => (
                        <div style={{display: "flex", flexDirection: "row", alignItems: "center", marginTop: '0.2vw'}}>
                            <img className={'friend-avatar'} src={friend.avatar} alt={'Friend Avatar'}/>
                            <text className={'friend-username'}>{friend.username}</text>
                            <Button onClick={() => invitePlayer(friend)}
                                    className={'friend-invite-button'}>Invite</Button>
                        </div>
                    ))}
                </table>}
                {friends.length === 0 && <text>You have no friends</text>}

            </div>
            <img className="friend-ico" src={friendIco} onClick={handleToggle} alt={"Amigos"}/>
            {/*<button onClick={useShowInvited}>show invited</button> for debugging*/}
        </div>
    );
};

export default FriendList;
