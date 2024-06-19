import React, { useEffect, useState } from 'react';
import '../static/css/home/ImageSidePanel.css';

const ImageSidePanel = ({ images }) => {
    const [shuffledImages, setShuffledImages] = useState([]);

    useEffect(() => {
        // Shuffle the images when the component mounts or when the images prop changes
        shuffleImages();
    }, [images]);

    const shuffleImages = () => {
        const shuffledImageList = [...images];
        for (let i = shuffledImageList.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [shuffledImageList[i], shuffledImageList[j]] = [shuffledImageList[j], shuffledImageList[i]];
        }
        setShuffledImages(shuffledImageList);
    };

    return (
        <div>
            {shuffledImages.map((image, index) => (
                <img className="panelImg" key={index} src={image} alt={`Image ${index}`} />
            ))}
        </div>
    );
};

export default ImageSidePanel;
