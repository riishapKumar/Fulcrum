const AWS = require('aws-sdk')


exports.handler = async (event, context) => {
    
    //add permissions

    const TargetArn = 'arn:aws:sns:us-west-2:099738709851:endpoint/GCM/Fulcrum/2c1591d4-7768-37d4-9329-aa380bccf586';
    
    let payload = {
        default:'default',
        GCM:{
            notification:{
                body:'Hello Rishap Thanks for Signing in ',
                title:'Fulcrum',
                sound:'default'
                
            }
        }
    }
    
    
    payload.GCM = JSON.stringify(payload.GCM);
    payload = JSON.stringify(payload)
    
    const param_sns =  {
        
        Message: payload,
        TargetArn: TargetArn,
        MessageStructure:'json'
        
    }
    
    const new_sns = new AWS.SNS({apiVersion:'2010-03-31'});
    await new_sns.publish(param_sns).promise();
    
    
    // TODO implement
    const response = {
        statusCode: 200,
        body: JSON.stringify('qqHello from Lambda!'),
    };
    return response;
};
