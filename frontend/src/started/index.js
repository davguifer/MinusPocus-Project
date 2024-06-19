import React from 'react';
import '../App.css';
import '../static/css/home/home.css';
import logo from '../static/images/Logo.png';

export default function Started() {
    return (
        <div>
            <div className='prueba-pruebisima'>
                <img alt='Logo' src={logo} style={{
                    marginBlockStart: 55,
                    marginLeft: 0,
                    alignSelf: 'start'
                }} />
            </div>
        </div>
    );
}