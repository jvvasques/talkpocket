import React, { Component } from 'react';
import EntryRow from '../EntryRow/entryRow'
import $ from 'jquery'

class TalkPocket extends Component {
  
    constructor(props) {
        super(props);

        this.state = {
            entries: [],
            urlInput: '',
            language: 'pt',
            translate: false
        };
        this.addNewEntry = this.addNewEntry.bind(this)
        this.updateEntriesStatus = this.updateEntriesStatus.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.handleLanguageChange = this.handleLanguageChange.bind(this)
        this.handleTranslateCheck = this.handleTranslateCheck.bind(this)
    }

    updateEntriesStatus () {        
        let self = this;                

        $.get( "http://localhost:8080/talk")
        .done(function( data ) {                            
            let responseJSON = JSON.parse(data)
            let entries = []

            responseJSON.map((entry) => {
                entries.push({
                    id: entry.id,
                    articleUrl: entry.file_url,
                    soundUrl: entry.file_id,
                    state: entry.state
                })
            })

            console.log(entries)                
            const newState = Object.assign({}, self.state, {entries})

            self.setState(newState)
        });

        console.log(this.state)

        console.log('Updating entries')
    }

    componentDidMount () {
        this.updateEntriesStatus();
        setInterval(this.updateEntriesStatus, 5000);
    }

    handleChange(event) {
        const newState = Object.assign({}, this.state, {urlInput: event.target.value})

        this.setState(newState)
    }

    handleLanguageChange(event) {
        const newState = Object.assign({}, this.state, {language: event.target.value})

        this.setState(newState)
    }

    handleTranslateCheck(event) {
        const newState = Object.assign({}, this.state, {translate: event.target.checked})

        this.setState(newState)
    }

    addNewEntry () {        
        let requestData = {"url": this.state.urlInput }
        if (this.state.translate)
        {
            requestData = Object.assign({}, requestData, {"lang": this.state.language})
        }

        console.log('Request data', requestData)

        $.ajax({
            url: "http://localhost:8080/talk",
            type: "POST",
            data: JSON.stringify(requestData),
            contentType: "application/json",            
            success: function(){
                console.log('sucess')
            }.bind(this),
            error: function(xhr, status, err) {
                console.log('error', status, err)
            }.bind(this)
        })

        const newState = Object.assign({}, this.state, {urlInput: ''})

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
                <br />
                <div className="ui checkbox">
                    <input type="checkbox" name="translate" defaultChecked={this.state.translate} onChange={this.handleTranslateCheck}/>
                    <label>Translate this text for me in &nbsp;</label>
                </div>
                <select value={this.state.language} onChange={this.handleLanguageChange}>
                    <option value="it">Italian</option>                    
                    <option value="pt">Portuguese</option>
                </select>
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
