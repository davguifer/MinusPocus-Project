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

export default function Field() {
    const currentUserId = tokenService.getUser().id;
    const pathSegments = window.location.pathname.split("/");
    const gameId = pathSegments[pathSegments.length - 1];
    const user = useFetchData("/api/v1/users/" + currentUserId, jwt);
    const [users, setUsers] = useState([]);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [game, setGame] = useIntervalFetchState([], `/api/v1/game/${gameId}`, jwt, setMessage, setVisible, null, 500);
    const playerStatusList = useIntervalFetchState([], `/api/v1/game/${gameId}/playerStatus`, jwt, setMessage, setVisible, null, 500);
    const [playerStatus, setPlayerStatus] = useIntervalFetchState([], '/api/v1/status/' + currentUserId, jwt, setMessage, setVisible, null, 500);
    const imageList = getAllImagenes();
    const [round, setRound] = useState(1);
    const [dejar, setDejar] = useState(false);
    const [handList, setHandList] = useState([]);
    const [usadas, setUsadas] = useState([]);
    const [noUsadas, setNoUsadas] = useState([]);
    const [advance, setAdvance] = useState(true)

    const spellList = game.spellsDeck ? game.spellsDeck.slice(0, 3).map((card) => {
        return (<td key={card.id}>
            <div className={"text-center"}
                style={{display: "flex", flexDirection: "column", background: "transparent"}}>
                <div style={{
                    width: "40%", maxWidth: "100%",alignSelf:"center"}}>
                    {buttonColor(card)}
                </div>
                <img src={elegirImagenSpell(card)} className={"spell-image"} alt={`Spell-${card}`}/>
                {isAlive(playerStatus) && <Button className={"discard-button"} onClick={() => {
                    discardSpell(card)
                }}>Discard</Button>}
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
                    <div style={{position: "absolute", marginLeft: "11%"}}>
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
        if (game.round > 1 && round < game.round) {
            setTimeout(function () {
                setRound(game.round);
            }, 750)
        }
        if ((game.spellsDeck && game.finish == null) ? game.spellsDeck.length === 0 : false) {
            shuffleSpells();
        }
        if (game.finish) {
            window.location.href = "/games";
        }
    }, [game]);

    useEffect(() => {
        if (!dejar && playerStatus.hand) {
            setNoUsadasCard(playerStatus.hand);
            setDejar(true);
        }
        let list = playerStatus.hand ? playerStatus.hand.map((card) => {
            return (elegirImagenIngredient(card) && isAlive(playerStatus)) ? <td key={card.id}>
                <div style={{display: "flex", flexDirection: "column", background: "transparent"}}>
                    {!isUsing(card) && <Button className={"card-select-button"} onClick={() => {
                        selectCard(card)
                    }}>Select</Button>}
                    {isUsing(card) && <Button className={"card-select-button deselect"} onClick={() => {
                        deselectCard(card)
                    }}>Deselect</Button>}
                    <img style = {{width: "50%"}} src={elegirImagenIngredient(card)} className={"ingredient-images"} alt={`Card-${card}`}></img>
                    <text className={"number-card-text"}>{numberUsing(card)}</text>
                    {!isUsing(card) && <Button className={"card-select-button sign"} onClick={() => {
                        cambiarSigno(card)
                    }}>{card.type === "BASE" ? "+ or -" : "x or /"}</Button>}
                </div>
            </td> : null;
        }) : null;
        setHandList(list);
    }, [playerStatus, usadas, noUsadas])

    useEffect(() => {

        if(round > 1 && dejar){
            setNoUsadasCard(playerStatus.hand)
        }

    }, [round])

    useEffect(() => {
        if ((users.length > 0) ? (users[0].id === user.id) : false) {
            let shuffle = false;
            let vote = true;
            let alives = 0;
            for (let ps of playerStatusList[0]) {
                if (isAlive(ps)) {
                    alives++
                    vote = vote && ps.vote
                }
                let max = 7 - ps.rabbits;
                if (max < 4) {
                    max = 4;
                }
                if (isAlive(ps) && ps.hand.length < max) {
                    shuffle = true;
                }
            }

            if (alives === 1) {
                fetch("/api/v1/game/" + game.id + '/finish', {
                    method: "PUT", headers: {
                        Authorization: `Bearer ${jwt}`, Accept: "application/json", "Content-Type": "application/json",
                    }
                }).then(window.location.href = "/games")
            } else if (shuffle && advance) {
                if (game.round < 3) {
                    setAdvance(false);
                    fetch("/api/v1/game/" + game.id + '/advance', {
                        method: "PUT", headers: {
                            Authorization: `Bearer ${jwt}`,
                            Accept: "application/json",
                            "Content-Type": "application/json",
                        }
                    }).then(setTimeout(function () {
                        setNoUsadasCard(playerStatus.hand);
                        setAdvance(true)
                    }, 750))
                } else if (game.ingredientsDeck.length === 0) {
                    fetch("/api/v1/game/" + game.id + '/finish', {
                        method: "PUT", headers: {
                            Authorization: `Bearer ${jwt}`,
                            Accept: "application/json",
                            "Content-Type": "application/json",
                        }
                    }).then(window.location.href = "/games")
                }
            } else if (game && playerStatusList[0].length > 0 && vote) {
                fetch("/api/v1/game/" + game.id + '/discard', {
                    method: "PUT", headers: {
                        Authorization: `Bearer ${jwt}`, Accept: "application/json", "Content-Type": "application/json",
                    }
                })
            }

        }
    }, [playerStatusList])

    function setNoUsadasCard(hand) {
        let lista = []
        if (hand) {
            hand.map((card) => {
                let dic = {}
                dic['ingredient'] = card;
                dic['sign'] = true;
                lista.push(dic)
            })
        }
        setNoUsadas(lista);
        setUsadas([]);
    }

    function shuffleSpells() {
        if (users[0].id === user.id) {
            fetch("/api/v1/game/" + game.id + '/shuffle', {
                method: "PUT", headers: {
                    Authorization: `Bearer ${jwt}`, Accept: "application/json", "Content-Type": "application/json",
                }
            })
        }
    }

    function buttonColor(card) {
        const colorMappings = {
            red: '#ff0707', yellow: '#f1d51d', green: '#75d037', blue: '#0cc5e5',
        }
        const darkenMappings = {
            red: '#b90505', yellow: '#bea814', green: '#579b29', blue: '#0993ab',
        }
        let res = [];
        for (let i = 0; i < playerStatusList[0].length; i++) {
            let ps = playerStatusList[0][i];
            if (ps.id !== playerStatus.id && card.target && isAlive(ps) && isAlive(playerStatus)) {
                res.push(<Button className={"spell-buttons"} style={{
                    backgroundColor: `${colorMappings[ps.color.toLowerCase()]}`,
                    color: "white",
                    borderColor: `${colorMappings[ps.color.toLowerCase()]}`
                }}
                                 onClick={() => {
                                     castSpell(card, ps.id)
                                 }}
                                 onMouseOver={(e) => {
                                     {
                                         e.target.style.backgroundColor = `${darkenMappings[ps.color.toLowerCase()]}`
                                         e.target.style.borderColor = `${darkenMappings[ps.color.toLowerCase()]}`// Adjust the percentage as needed
                                     }
                                 }}
                                 onMouseOut={(e) => {
                                     {
                                         e.target.style.backgroundColor = `${colorMappings[ps.color.toLowerCase()]}`
                                         e.target.style.borderColor = `${colorMappings[ps.color.toLowerCase()]}`
                                     }
                                 }}>Select</Button>)

            }
            if (ps.id === playerStatus.id && !card.target && isAlive(playerStatus)) {
                res.push(<Button className={"spell-buttons"} style={{
                    backgroundColor: `${colorMappings[ps.color.toLowerCase()]}`,
                    borderColor: `${colorMappings[ps.color.toLowerCase()]}`
                }}
                                 onClick={() => {
                                     castSpell(card, ps.id)
                                 }}
                                 onMouseOver={(e) => {
                                     {
                                         e.target.style.backgroundColor = `${darkenMappings[ps.color.toLowerCase()]}`
                                         e.target.style.borderColor = `${darkenMappings[ps.color.toLowerCase()]}`// Adjust the percentage as needed
                                     }
                                 }}
                                 onMouseOut={(e) => {
                                     {
                                         e.target.style.backgroundColor = `${colorMappings[ps.color.toLowerCase()]}`
                                         e.target.style.borderColor = `${colorMappings[ps.color.toLowerCase()]}`
                                     }
                                 }}>Select</Button>)
            }
        }
        return res;
    }

    function isAlive(ps) {
        return ps.w1 + ps.w2 + ps.w3 + ps.w4 > 0;
    }

    function isUsing(card) {
        for (let i = 0; i < usadas.length; i++) {
            if (usadas[i]['ingredient'].id === card.id) {
                return true
            }
        }
    }

    function numberUsing(card) {
        let res = ""
        for (let i = 0; i < usadas.length; i++) {
            if (usadas[i]['ingredient'].id === card.id) {
                res = (i + 1)
            }
        }
        return res;
    }

    function selectCard(card) {
        let listaUsada = [...usadas]
        let listaNoUsada = [...noUsadas]
        let position
        for (let i = 0; i < noUsadas.length; i++) {
            if (noUsadas[i]['ingredient'].id === card.id) {
                position = i
            }
        }
        let cardToEliminate = listaNoUsada[position]
        listaUsada.push(cardToEliminate)
        listaNoUsada.splice(position, 1)
        setUsadas(listaUsada)
        setNoUsadas(listaNoUsada)
    }

    function deselectCard(card) {
        let listaUsada = [...usadas]
        let listaNoUsada = [...noUsadas]
        let position
        for (let i = 0; i < usadas.length; i++) {
            if (usadas[i]['ingredient'].id === card.id) {
                position = i
            }
        }
        for (let i = listaUsada.length - 1; i >= position; i--) {
            let cardToEliminate = listaUsada[i]
            listaNoUsada.push(cardToEliminate)
            listaUsada.splice(i, 1);
        }
        setUsadas(listaUsada);
        setNoUsadas(listaNoUsada);
    }

    function calcOperation() {
        let count = 0;
        for (let i = 0; i < usadas.length; i++) {
            let c = usadas[i];
            if (c.ingredient.type === "BASE") {
                count += c.ingredient.valuable * (c.sign ? 1 : -1);
            } else {
                let mult = c.sign ? c.ingredient.valuable : 1 / c.ingredient.valuable;
                let sum = 0;
                for (; i + 1 === usadas.length ? false : usadas[i + 1].ingredient.type === "ARTEFACT";) {
                    i++;
                    c = usadas[i];
                    mult = mult * (c.sign ? c.ingredient.valuable : 1 / c.ingredient.valuable);
                }
                for (let j = 1; i + 1 === usadas.length ? false : usadas[i + 1].ingredient.type === "BASE" && j < 4; j++) {
                    i++;
                    c = usadas[i];
                    sum += c.ingredient.valuable * (c.sign ? 1 : -1);
                }
                count += mult * sum;
            }
        }

        return count;
    }

    function cambiarSigno(card) {
        for (let i = 0; i < noUsadas.length; i++) {
            if (noUsadas[i]['ingredient'].id === card.id) {
                noUsadas[i]['sign'] = !noUsadas[i]['sign'];
            }
        }
    }

    function elegirImagenIngredient(card) {
        let image = "/static/media/CartaValor";
        for (let i = 0; i < noUsadas.length; i++) {
            if (noUsadas[i]['ingredient'].id === card.id) {
                if (noUsadas[i]['ingredient'].type === "BASE") {
                    if (noUsadas[i]['sign']) {
                        image = image + "Mas" + noUsadas[i]['ingredient'].valuable;
                    } else {
                        image = image + "Menos" + noUsadas[i]['ingredient'].valuable;
                    }
                } else {
                    if (noUsadas[i]['sign']) {
                        image = image + "Por" + noUsadas[i]['ingredient'].valuable;
                    } else {
                        image = image + "Entre" + noUsadas[i]['ingredient'].valuable;
                    }
                }
            }
        }
        for (let i = 0; i < usadas.length; i++) {
            if (usadas[i]['ingredient'].id === card.id) {
                if (usadas[i]['ingredient'].type === "BASE") {
                    if (usadas[i]['sign']) {
                        image = image + "Mas" + usadas[i]['ingredient'].valuable;
                    } else {
                        image = image + "Menos" + usadas[i]['ingredient'].valuable;
                    }
                } else {
                    if (usadas[i]['sign']) {
                        image = image + "Por" + usadas[i]['ingredient'].valuable;
                    } else {
                        image = image + "Entre" + usadas[i]['ingredient'].valuable;
                    }
                }
            }
        }

        if (image === "/static/media/CartaValor") {
            return null;
        } else {
            for (let i = 0; i < imageList.length; i++) {
                if (imageList[i].includes(image)) {
                    return imageList[i];
                }
            }
        }
    }

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

    function castSpell(card, playerStatusId) {
        const count = calcOperation();
        if (count === card.valuable) {
            fetch("/api/v1/spells/" + card.id + '/cast', {
                method: "PUT", headers: {
                    Authorization: `Bearer ${jwt}`, Accept: "application/json", "Content-Type": "application/json",
                }, body: JSON.stringify({
                    "psIds": (card.target ? [playerStatus.id, playerStatusId] : [playerStatus.id]),
                    "ingredients": usadas.map((u) => u['ingredient'])
                })
            })
                .then((response) => response.json())
                .then((responseData) => setNoUsadasCard(responseData.hand))

        } else {
            fetch(`/api/v1/status/${playerStatus.id}/punish`, {
                method: "PUT", headers: {
                    Authorization: `Bearer ${jwt}`, Accept: "application/json", "Content-Type": "application/json",
                }
            })
        }
    }

    function discardSpell(card) {
        const count = calcOperation();
        if (count === 7) {
            fetch("/api/v1/spells/" + card.id + '/discard', {
                method: "PUT", headers: {
                    Authorization: `Bearer ${jwt}`, Accept: "application/json", "Content-Type": "application/json",
                }, body: JSON.stringify({
                    "gameId": Number(gameId),
                    "playerStatusId": playerStatus.id,
                    "ingredients": usadas.map((u) => u['ingredient'])
                })
            })
                .then((response) => response.json())
                .then((responseData) => setNoUsadasCard(responseData.hand))

        } else {
            fetch(`/api/v1/status/${playerStatus.id}/punish`, {
                method: "PUT", headers: {
                    Authorization: `Bearer ${jwt}`, Accept: "application/json", "Content-Type": "application/json",
                }
            })
        }
    }

    function discardAll() {
        fetch("/api/v1/status/" + playerStatus.id + "/discard", {
            method: "PUT", headers: {
                Authorization: `Bearer ${jwt}`, Accept: "application/json", "Content-Type": "application/json",
            }
        })
            .then((response) => response.json())
            .then((responseData) => setNoUsadasCard(responseData.hand))
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
        <Table>
            <tbody>
            {statusList}
            </tbody>
        </Table>
        <div>
            <Table>
                <tbody>
                {spellList}
                </tbody>
            </Table>
        </div>
        <div>
            <div style={{display: "flex", flexDirection: "row", maxWidth: "20%"}}>
                <img src={'https://cdn-icons-png.flaticon.com/512/802/802338.png'} className={"stats-images"}
                     alt='Conejo'/>
                <text className={"stats-info"}>{playerStatus.rabbits}</text>
                <img src={escudo}
                     className={"stats-images"} alt='barrera'/>
                <text className={"stats-info"}>{playerStatus.barrier}</text>
                {isAlive(playerStatus) && <Button className={"vote-button"} onClick={() => {
                    fetch("/api/v1/status/" + playerStatus.id + "/vote", {
                        method: "PUT", headers: {
                            Authorization: `Bearer ${jwt}`,
                            Accept: "application/json",
                            "Content-Type": "application/json",
                        }
                    })
                }}>Discard spells?</Button>}
                <text className={"stats-info"}>{"" + playerStatus.vote}</text>
            </div>
            <div style={{display: "flex", flexDirection: "row", maxWidth: "100%"}}>
                <div>
                    <img src={elegirImagenW1(playerStatus)} className={"wizards"} alt={`W1-${playerStatus}`}/>
                    <img src={elegirImagenW2(playerStatus)} className={"wizards"} alt={`W2-${playerStatus}`}/>
                    <img src={elegirImagenW3(playerStatus)} className={"wizards"} alt={`W3-${playerStatus}`}/>
                    <img src={elegirImagenW4(playerStatus)} className={"wizards"} alt={`W4-${playerStatus}`}/>
                </div>
                <div style={{marginTop: "-2%"}}>
                    {handList}
                </div>
                {isAlive(playerStatus) && <Button className={"discard-button player"} onClick={() => {
                    discardAll()
                }}>Discard Ingredients</Button>}
            </div>
            <div style={{marginTop: "-3%"}}>
                {isAlive(playerStatus) && <Button className={"exit-button"} onClick={() => {
                    window.location.href = "/games"
                }}>
                    Left game
                </Button>}
                {!isAlive(playerStatus) && <Button className={"exit-button not-alive"} onClick={() => {
                    window.location.href = "/games"
                }}>
                    Left game
                </Button>}
            </div>

        </div>

    </div>);
}
