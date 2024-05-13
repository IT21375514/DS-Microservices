import React from 'react';

const PaymentHistory = () => {
    // Mock data for payment history
    const paymentHistoryData = [
        { userName: 'User1', courseId: 'Application Framework', payment: 1000, paymentId: '53253466' },
        { userName: 'User2', courseId: 'Distributed Systems', payment: 2000, paymentId: '362432643' },
        { userName: 'User3', courseId: 'SEPQM', payment: 2000, paymentId: '32646234' },
        { userName: 'User4', courseId: 'Software Architecture', payment: 4000, paymentId: '364233473' },
        { userName: 'User5', courseId: 'Industry Placement', payment: 3000, paymentId: '213454612' }
    ];

    return (
        <div className="container">
            <h2 className="text-center"><strong>Payment History</strong></h2>
            <div className="row mt-5">
                {paymentHistoryData.map((payment, index) => (
                    <div key={index} className="col-md-12 mb-3" style={{border:"2px solid #000000", padding:"2em 1em"}}>
                        <div className="row">
                            <div className="col-md-4">
                                <p><strong>User Name:</strong> {payment.userName}</p>
                            </div>
                            <div className="col-md-4">
                                <p><strong>Course ID:</strong> {payment.courseId}</p>
                            </div>
                            <div className="col-md-4">
                                <p><strong>Payment:</strong> {payment.payment}</p>
                                <p><strong>Payment ID:</strong> {payment.paymentId}</p>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default PaymentHistory;
