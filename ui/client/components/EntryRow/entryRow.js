import React, { Component, PropTypes } from 'react';

class EntryRow extends Component {

  render() {
    return (
        <div>
            asasds
            // <span>Article: {articleUrl}</span>            
            // <span>Sound: {soundUrl}</span>
            // <span>Image: {image}</span>
        </div>
    );
  }
}

EntryRow.propTypes = {
    articleUrl: PropTypes.string.isRequired,
    soundUrl: PropTypes.string,
    image: PropTypes.string
};

export default EntryRow;
