import React, { Component } from 'react';
import TalkPocket from '../TalkPocket/talkpocket'

class IndexComponent extends Component {
  render() {
    return (
      <section>
        <h2>Talk Pocket</h2>
        <TalkPocket />
      </section>
    );
  }
}

IndexComponent.defaultProps = {
  items: []
};

export default IndexComponent;
