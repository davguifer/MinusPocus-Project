// En modalForm.js
import React, { useState } from "react";
import { Button, Modal, ModalBody, ModalFooter, ModalHeader, Form, Label, Input } from "reactstrap";

export default function ModalForm(setVisible, visible = false, message = null, setCodigo, codigo ) {
    const [inputCodigo, setInputCodigo] = useState(codigo);

    function handleVisible() {
        setVisible(!visible);
    }

    function handleChange(event) {
        setInputCodigo(event.target.value);
    }

    function handleSubmit(event) {
        event.preventDefault();
        setCodigo(inputCodigo);
        handleVisible();
    }

    if (message) {
        const closeBtn = (
            <button className="close" onClick={() => { handleVisible(); setCodigo(''); }} type="button">
                &times;
            </button>
        );
        return (
            <div>
                <Modal isOpen={visible} toggle={handleVisible} keyboard={false}>
                    <ModalHeader toggle={handleVisible} close={closeBtn}>Codigo</ModalHeader>
                    <ModalBody>
                        <div className="auth-form-container">
                            <Form onSubmit={handleSubmit}>
                                <div className="custom-form-input">
                                    <Label for="codigo" className="custom-form-input-label">
                                        Codigo
                                    </Label>
                                    <Input
                                        type="text"
                                        required
                                        name="codigo"
                                        id="codigo"
                                        onChange={handleChange}
                                        className="custom-input"
                                    />
                                    <Button>Save</Button>
                                </div>
                            </Form>
                        </div>
                    </ModalBody>
                    <ModalFooter>
                        <Button color="primary" onClick={handleVisible}>Close</Button>
                    </ModalFooter>
                </Modal>
            </div>
        )
    } else
        return <></>;
}
