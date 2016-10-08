import React, { Component } from 'react';
import TalkPocket from '../TalkPocket/talkpocket'
import LogoImg from '../../images/logo.png';

class IndexComponent extends Component {
  render() {
    return (
      <section>   
        <div style={{display: 'flex', justifyContent: 'center'}}>
            <img src={LogoImg} width={200} height={99}/>
        </div>
        <br /><br />
        <TalkPocket />
      </section>
    );
  }
}

IndexComponent.defaultProps = {
  items: []
};

export default IndexComponent;
