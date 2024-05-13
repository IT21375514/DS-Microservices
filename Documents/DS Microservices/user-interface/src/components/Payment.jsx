import React from 'react';
import { Container, Row, Form, Button } from 'react-bootstrap';
import { FaPaypal, FaCreditCard } from 'react-icons/fa';

const Payment = () => {
    return (
        <section id="payment">
            <Container>
                <div className="text-center mb-1">
                    <img src='img/logo-black-logo.png' alt="Logo" height="80" />
                </div>
                <div className="ImgContainer mb-5">
                    <img src='img/logo-black-text.png' alt="mar-demo-pic" className="bgImage" style={{ maxWidth: '400px', margin:'auto' }} />
                </div>
                <div id="payment-container">
                    <Row className="text-center mb-5">
                        <h2>Payment Portal</h2>
                    </Row>
                    <div className='mb-3'>
                        <div className="row">
                            <div className="col-md-3">User ID</div>
                            <div className="col-md-9">xxxxxx</div>
                        </div>
                        <div className="row">
                            <div className="col-md-3">Course ID</div>
                            <div className="col-md-9">xxxxxx</div>
                        </div>
                    </div>
                    <div className='mb-3 row'>
                        <div className="col-md-3">
                            <Form.Label>Pay</Form.Label>
                        </div>
                        <div className="col-md-9">
                            <Form.Group controlId="formPay">
                                <Form.Control type="text" placeholder="Enter amount to pay" />
                            </Form.Group>
                        </div>
                    </div>
                    <div className='mb-3 row'>
                        <div className="col-md-3">
                            <Form.Label>Currency</Form.Label>
                        </div>
                        <div className="col-md-9">
                            <Form.Group controlId="formCurrency">
                                <Form.Control type="text" placeholder="Enter currency" />
                            </Form.Group>
                        </div>
                    </div>
                    <div className='mb-3 row'>
                        <div className="col-md-3">
                            <Form.Label>Remarks</Form.Label>
                        </div>
                        <div className="col-md-9">
                            <Form.Group controlId="formDescription">
                                <Form.Control type="text" placeholder="Enter remarks" />
                            </Form.Group>
                        </div>
                    </div>
                    <div className='mb-3 row'>
                        <div className="col-md-3">
                            <Form.Label>Payment method</Form.Label>
                        </div>
                        <div className="col-md-9">
                            <div className="row">
                                <div className="col">
                                    <Form.Check
                                        type="radio"
                                        id="paypalRadio"
                                        label={<><FaPaypal /> PayPal</>}
                                        name="paymentMethod"
                                    />
                                </div>
                            </div>
                            <div className="row">
                                <div className="col">
                                    <Form.Check
                                        type="radio"
                                        id="creditCardRadio"
                                        label={<><FaCreditCard /> Credit Card</>}
                                        name="paymentMethod"
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="d-flex justify-content-end mb-3">
                        <Button variant="primary" type="submit">
                            Pay Now
                        </Button>
                    </div>
                </div>
            </Container>
        </section>
    );
}

export default Payment;
