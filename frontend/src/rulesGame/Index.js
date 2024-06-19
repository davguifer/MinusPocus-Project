import React, { useState } from 'react';
import '../App.css';
import '../static/css/home/home.css';
import instruccion1 from '../static/images/Instrucciones1.jpg';
import instruccion2 from '../static/images/Instrucciones2.jpg';
import instruccion3 from '../static/images/Instrucciones3.jpg';
import { Button } from 'reactstrap';
import '../static/css/rules.css'
import {FaArrowLeftLong} from "react-icons/fa6";

export default function RulesGame() {
    const [index, setIndex] = useState(0);
    const listImg = [instruccion1, instruccion2, instruccion3];

    function nextPage() {
        if (index < listImg.length - 1) {
            setIndex(index + 1);
        }
    }

    function beforePage() {
        if (index > 0) {
            setIndex(index - 1);
        }
    }

    return (
        <div className='prueba-pruebisima'>
            <Button
                className="rules-action-button"
                onClick={beforePage}
            >Página Anterior
            </Button>
            <img
                className="rules-img"
                alt='Instrucciones'
                src={listImg[index]}
            />
            <Button
                className="rules-action-button"
                onClick={nextPage}
            >
                Página siguiente
            </Button>
        </div>
    );
}
