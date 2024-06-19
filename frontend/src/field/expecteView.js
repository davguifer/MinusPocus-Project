import {useState, useEffect} from "react";
import tokenService from "../services/token.service";
import useIntervalFetchState from "../util/useIntervalFetchState";
import {Button, Table} from "reactstrap";
import useFetchData from "../util/useFetchData";
import '../static/css/home/home.css'
import '../static/css/field.css'
import escudo from '../static/images/shield.png'
import getAllImagenes from "../util/getAllImagenes";

const jwt = tokenService.getLocalAccessToken();

export default function ExpecteView(){

    const currentUserId = tokenService.getUser().id;
    const pathSegments = window.location.pathname.split("/");
    const gameId = pathSegments[pathSegments.length - 2];
    const user = useFetchData("/api/v1/users/" + currentUserId, jwt);
    const [users, setUsers] = useState([]);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [game, setGame] = useIntervalFetchState([], `/api/v1/game/${gameId}`, jwt, setMessage, setVisible, null, 500);
    const playerStatusList = useIntervalFetchState([], `/api/v1/game/${gameId}/playerStatus`, jwt, setMessage, setVisible, null, 500);
    const imageList = getAllImagenes();

    const spellList = game.spellsDeck ? game.spellsDeck.slice(0, 3).map((card) => {
        return (<td key={card.id}>
            <div className={"text-center"}
                style={{display: "flex", flexDirection: "column", background: "transparent"}}>
                <img src={elegirImagenSpell(card)} className={"spell-image"}
                     alt={`Spell-${card}`}/>
            </div>
        </td>);
    }) : null;

    let statusList = [];
    if(playerStatusList[0].length > 0 && !game.finish) {

        statusList = playerStatusList[0].map((status) => {
            if (status.player.id !== user.id) {
                const userWidth = 100 / playerStatusList[0].length;
                return (<td key={status.id} style={{
                    backgroundColor: "transparent", width: `${userWidth}%`
                }}>

                <div className={"text-center"} style={{background: "transparent",marginTop:"-2%"}}>
                    <text className={"field-username"}>{status.player.username}</text>
                </div>
                <div style={{backgroundColor: "transparent"}}>
                    <div style={{position: "absolute", marginLeft: '7%'}}>
                        <img className={"enemy-image"} src={'https://cdn-icons-png.flaticon.com/512/802/802338.png'} alt='Conejo'/>
                        <h4 className={"enemy-info"}>{status.rabbits}</h4>
                        <img className={"enemy-image"} src={escudo}/>
                        <h4 className={"enemy-info"}>{status.barrier}</h4>
                    </div>
                    <div style={{display: "flex", flexDirection: "column"}}>
                        <img alt="" src={status.player.avatar} className={"field-avatar text-center"}/>
                        <text className={"vote-info"}>{"Discard vote: " + status.vote}</text>
                    </div>
                </div>
                <div className={"text-center"} style={{background: "transparent"}}>
                    <img src={elegirImagenW1(status)} className={"enemy-wizards"} alt={`W1-${status}`}/>
                    <img src={elegirImagenW2(status)} className={"enemy-wizards"} alt={`W2-${status}`}/>
                    <img src={elegirImagenW3(status)} className={"enemy-wizards"} alt={`W3-${status}`}/>
                    <img src={elegirImagenW4(status)} className={"enemy-wizards"} alt={`W4-${status}`}/>
                </div>
            </td>)
        }

        })
    }

    useEffect(() => {
        if (game && game.users) {
            setUsers(game.users);
        }
        if (game.finish) {
            window.location.href = "/games";
        }
    }, [game]);

    function elegirImagenSpell(card) {
        let image = "/static/media/Conjuro" + card.valuable;
        for (let i = 0; i < imageList.length; i++) {
            if (imageList[i].includes(image)) {
                return imageList[i];
            }
        }
    }

    function elegirImagenW1(ps) {
        let image = '/static/media/Maga'
        if (ps.color === 'RED') {
            image += 'RojaEncapuchada'
        } else if (ps.color === 'BLUE') {
            image += 'AzulEncapuchada'
        } else if (ps.color === 'GREEN') {
            image += 'VerdeEncapuchada'
        } else {
            image += 'AmarillaEncapuchada'
        }
        if (ps.w1 === 2) {
            image += 'Frontal'
        } else if (ps.w1 === 1) {
            image += 'Reverso'
        } else {
            image = '/static/media/ParteTraseraConjuro'
        }
        for (let i = 0; i < imageList.length; i++) {
            if (imageList[i].includes(image)) {
                return imageList[i];
            }
        }
    }

    function elegirImagenW2(ps) {
        let image = '/static/media/Maga'
        if (ps.color === 'RED') {
            image += 'RojaPelo'
        } else if (ps.color === 'BLUE') {
            image += 'AzulPelo'
        } else if (ps.color === 'GREEN') {
            image += 'VerdePelo'
        } else {
            image += 'AmarillaPelo'
        }
        if (ps.w2 === 2) {
            image += 'Frontal'
        } else if (ps.w2 === 1) {
            image += 'Reverso'
        } else {
            image = '/static/media/ParteTraseraConjuro'
        }
        for (let i = 0; i < imageList.length; i++) {
            if (imageList[i].includes(image)) {
                return imageList[i];
            }
        }
    }

    function elegirImagenW3(ps) {
        let image = '/static/media/Mago'
        if (ps.color === 'RED') {
            image += 'RojoCalvo'
        } else if (ps.color === 'BLUE') {
            image += 'AzulCalvo'
        } else if (ps.color === 'GREEN') {
            image += 'VerdeCalvo'
        } else {
            image += 'AmarilloCalvo'
        }
        if (ps.w3 === 2) {
            image += 'Frontal'
        } else if (ps.w3 === 1) {
            image += 'Reverso'
        } else {
            image = '/static/media/ParteTraseraConjuro'
        }
        for (let i = 0; i < imageList.length; i++) {
            if (imageList[i].includes(image)) {
                return imageList[i];
            }
        }
    }

    function elegirImagenW4(ps) {
        let image = '/static/media/Mago'
        if (ps.color === 'RED') {
            image += 'RojoCapucha'
        } else if (ps.color === 'BLUE') {
            image += 'AzulCapucha'
        } else if (ps.color === 'GREEN') {
            image += 'VerdeCapucha'
        } else {
            image += 'AmarilloCapucha'
        }
        if (ps.w4 === 2) {
            image += 'Frontal'
        } else if (ps.w4 === 1) {
            image += 'Reverso'
        } else {
            image = '/static/media/ParteTraseraConjuro'
        }
        for (let i = 0; i < imageList.length; i++) {
            if (imageList[i].includes(image)) {
                return imageList[i];
            }
        }
    }

    return (<div className="fondo-Coliseo">
        <div className={"text-position"}>
            <h5 className={"field-info-text round-text text-center"}>Round: {game.round}</h5>
            <div style={{position: "absolute"}}>
                <h5 className={"field-info-text remaining-info"}>Spells in
                    deck: {game.spellsDeck ? (game.spellsDeck.length <= 3 ? 0 : game.spellsDeck.length - 3) : 0}</h5>
                <h5 className={"field-info-text remaining-info"}>Ingredients in
                    deck: {game.ingredientsDeck ? game.ingredientsDeck.length : 0}</h5>
            </div>
        </div>
        <div style={{marginTop: 100}}>
            <Table>
                <tbody>
                {statusList}
                </tbody>
            </Table>
        </div>
        <div>
            <Table>
                <tbody>
                {spellList}
                </tbody>
            </Table>
        </div>
        <div style={{marginTop: "-1%"}}>
            <Button className={"exit-button not-alive"} onClick={() => {
                window.location.href = "/games"
            }}>
                Left game
            </Button>
        </div>
    </div>);
}