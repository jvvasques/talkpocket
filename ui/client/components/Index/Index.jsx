import React, { Component } from 'react';
import TalkPocket from '../TalkPocket/talkpocket'

class IndexComponent extends Component {
  render() {
    return (
      <section>
          <h1 className="ui header">TalkPocket</h1>
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
