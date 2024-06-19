// Home.js
import React, {useEffect, useState} from 'react';
import '../App.css';
import '../static/css/home/home.css';
import ImageSidePanel from './ImageSidePanel'
import logo from '../static/images/Logo.png';
import tokenService from '../services/token.service';
import {Button} from 'reactstrap';
import '../static/css/home/ImageSidePanel.css'
import getAllImagenes from "../util/getAllImagenes";
import FriendList from "../friends/friendList";


export default function Home() {
    const authority = tokenService.getUser() !== null ? tokenService.getUser().roles[0] : '';
    const imageList = getAllImagenes();

    return (<div>
        <div className="home-container">
            <div className="prueba-pruebisima">
                <div className="hero-div">
                    <img className="logoImg" alt="Logo Minus Pocus" src={logo}/>
                    {authority == 'PLAYER' && <div className="button-container">
                        <Button className="action-button" onClick={() => (window.location.href = '/CreateGames')}>
                            Crear Partida
                        </Button>
                        <Button className="action-button" onClick={() => (window.location.href = '/games')}>
                            Unirse Partida
                        </Button>
                    </div>}
                </div>
            </div>
            <div className="left-panel"><ImageSidePanel images={imageList}/></div>
            <div className="right-panel"><ImageSidePanel images={imageList}/></div>


        </div>


    </div>);
}
