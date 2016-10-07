import React, { Component } from 'react';
import EntryRow from '../EntryRow/entryRow'

class TalkPocket extends Component {
  
    constructor(props) {
        super(props);

        this.state = {
            entries: [
            ],
            urlInput: 'sdasd'
        };

        this.addNewEntry = this.addNewEntry.bind(this)
        this.updateEntriesStatus = this.updateEntriesStatus.bind(this)
        this.handleChange = this.handleChange.bind(this)
    }

    updateEntriesStatus () {
        // $.ajax({
        //     url: this.props.url,
        //     dataType: 'json',
        //     cache: false,
        //     success: function(data) {
        //         this.setState({data: data});
        //     }.bind(this),
        //     error: function(xhr, status, err) {
        //         console.error(this.props.url, status, err.toString());
        //     }.bind(this)
        // });

        console.log('Updating entries')
    }

    componentDidMount () {
        this.updateEntriesStatus();
        setInterval(this.updateEntriesStatus, 2000);
    }

    handleChange(event) {
        const newState = Object.assign({}, this.state, {urlInput: event.target.value})

        this.setState(newState)
    }

    addNewEntry () {
        var entries = this.state.entries.push({ articleUrl: this.state.urlInput})

        const newState = Object.assign({}, this.state, entries)

        this.setState(newState)
    }

    render() {
        return (
            <div>
                <div className="ui fluid labeled action input">
                    <div className="ui label">
                        http://
                    </div>
                    <input id='' type="text"                     
                        placeholder="Enter an URL to pocket..." 
                        value={this.state.urlInput}
                        onChange={this.handleChange} />
                    <button className="ui button" onClick={this.addNewEntry}>Get Audio</button>
                </div>
                <br /><br />                
                <table className="ui celled striped table">
                    <thead>
                        <tr><th colSpan="3">
                        List of URLs
                        </th>
                    </tr></thead>
                    <tbody>
                    {
                        this.state.entries.map((e, index) =>
                        [
                            <EntryRow {...e} />                 
                        ])                     
                    }                                   
                    </tbody>
                </table>
            </div>
        );
    }
}

export default TalkPocket;
