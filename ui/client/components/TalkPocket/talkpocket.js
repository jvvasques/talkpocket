import React, { Component } from 'react';
import EntryRow from '../EntryRow/entryRow'

class TalkPocket extends Component {
  
    constructor(props) {
        super(props);
        this.state = {
            entries: [
                { articleUrl: 'http://blabla', soundUrl: 'http://sakdjlksajfl', image: 'image.png'},
                { articleUrl: 'http://blabl2a', soundUrl: 'http://sakdjlksajfl2', image: 'imag2e.png'}
            ]
        };
    }

    render() {
        return (
            <div>

                Insert pocket url:
                <input id='pocketUrl' value=''/>
                <button>Get audio</button>

                <br /><br />
                List:
                {this.state.entries.length}

                <br />                
                {
                    this.state.entries.map((e) => {                        
                        <EntryRow articleUrl={e.articleUrl} />
                    })
                }
            </div>
        );
    }
}

export default TalkPocket;
