import React, { useState } from 'react';
import '../App.css';
import '../static/css/home/home.css';
import { Table } from "reactstrap";
import tokenService from '../services/token.service';
import '../static/css/home/ImageSidePanel.css'
import useFetchData from "../util/useFetchData";
import useFetchState from "../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function Ranking() {
    const ranking = useFetchData(`/api/v1/stats/ranking`,jwt);
    const lista = []
    const punctuation = []
    for(let s in ranking){
        const users = {}
        users['Punctuation'] = s
        users['User'] = ranking[s]
        lista.unshift(users)
    }

    const rankingList = lista.slice(2, (lista.length-1)).map((user) => {
        return (
            <tbody key={user['User'].id}>
            <tr>
                <th scope="row">{user['User'].username}</th>
                <td>{user['Punctuation']} pts</td>
            </tr>
            </tbody>
        );
    })

    return (
        <div className="prueba-pruebisima">
            {lista.length > 0 && <div>
                <h1 style={{color: 'gold', marginTop: '-160%', marginLeft: '150%'}}>Primero</h1>
                <h4 style={{color: 'black', marginTop: '0%', marginLeft: '180%'}}>{lista[0]['User'].username}</h4>
                <h4 style={{color: 'black', marginTop: '10%', marginLeft: '135%'}}>Puntuación:</h4>
                <h4 style={{color: 'black', marginTop: '-22%', marginLeft: '225%'}}>{lista[0]['Punctuation']} </h4>
                <h4 style={{color: 'black', marginTop: '-22%', marginLeft: '247%'}}>ps</h4>
                <img alt="" src={lista[0]['User'].avatar} style={{ height: 100, width: 100 ,display: 'block', marginLeft: '170%'}} />
            </div>}
            {lista.length > 0 && <div>
                <h1 style={{color: 'silver', marginTop: '-80%', marginLeft: '-200%'}}>Segundo</h1>
                <h4 style={{color: 'black', marginTop: '0%', marginLeft: '-185%'}}>{lista[1]['User'].username}</h4>
                <h4 style={{color: 'black', marginTop: '10%', marginLeft: '-215%'}}>Puntuación: {lista[1]['Punctuation']} pts</h4>
                <img alt="" src={lista[1]['User'].avatar} style={{ height: 100, width: 100 ,display: 'block', marginLeft: '-190%'}} />
            </div>}
            {lista.length > 0 && <div>
                <h1 style={{color: 'brown', marginTop: '-50%', marginLeft: '220%'}}>Tercero</h1>
                <h4 style={{color: 'black', marginTop: '0%', marginLeft: '250%'}}>{lista[2]['User'].username}</h4>
                <h4 style={{color: 'black', marginTop: '10%', marginLeft: '205%'}}>Puntuación:</h4>
                <h4 style={{color: 'black', marginTop: '-22%', marginLeft: '302%'}}>{lista[2]['Punctuation']} </h4>
                <h4 style={{color: 'black', marginTop: '-22%', marginLeft: '325%'}}>ps</h4>
                <img alt="" src={lista[2]['User'].avatar} style={{ height: 100, width: 100 ,display: 'block', marginLeft: '240%'}} />
            </div>}
            {lista.length > 0 && <div>
                <h4 style={{color: 'black', marginTop: '200%', marginLeft: '-150%'}}>Top 50</h4>
                <Table style={{marginTop: '0%', marginLeft:'-175%'}}>
                    {rankingList}
                </Table>
            </div>}
        </div>
    );
}
